package controllers.security.rest

import javax.inject.Inject
import utils.responses.rest._
import services.UserService
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.core._
import com.mohiva.play.silhouette.core.services._
import com.mohiva.play.silhouette.core.providers._
import com.mohiva.play.silhouette.core.utils.PasswordHasher
import com.mohiva.play.silhouette.contrib.authenticators.HeaderAuthenticator
import scala.concurrent.Future

import security.models._
import models.users._

/**
 * This controller manage registration of an user
 */
class RestSignUpController @Inject() (
  implicit val env: Environment[User, HeaderAuthenticator],
  val userService: UserService,
  val authInfoService: AuthInfoService,
  val avatarService: AvatarService,
  val passwordHasher: PasswordHasher) extends Silhouette[User, HeaderAuthenticator] {

  /**
   * The formats for read json represent user
   */
  implicit val restFormat = formatters.json.UserFormats.restFormat
  implicit val signUpFormat = Json.format[SignUp]

  /**
   * Registers a new user.
   *
   * receive call with json like this:
   * 	{
   * 		"password": "",
   * 		"identifier": "",
   *  		"firstName": "",
   *    	"lastName": "",
   *     	"fullName": ""
   * 	}
   *
   * @return The result to display.
   */
  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp] match {
      case JsSuccess(signUp, _) =>
        val loginInfo = LoginInfo(CredentialsProvider.Credentials, signUp.identifier)
        (userService.retrieve(loginInfo).map {
          case None => /* user not already exists */
            val authInfo = passwordHasher.hash(signUp.password)
            val user = createUser(loginInfo, signUp)
            (for {
              avatar <- avatarService.retrieveURL(signUp.identifier)
              user <- userService.save(user.copy(avatarUrl = avatar))
              authInfo <- authInfoService.save(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(user)
            } yield {
              env.eventBus.publish(SignUpEvent(user, request, request2lang))
              env.eventBus.publish(LoginEvent(user, request, request2lang))
              val response = Ok(Json.toJson(Token(token = authenticator.id, expiresOn = authenticator.expirationDate)))
              env.authenticatorService.init(authenticator, Future.successful(response))
            }).flatMap { r => r }
          case Some(u) => /* user already exists! */
            Future.successful(Conflict(Json.toJson(Bad(message = "user already exists"))))
        }).flatMap { r => r }
      case JsError(e) => Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toFlatJson(e)))))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(identity) =>
        env.authenticatorService.create(identity).flatMap { authenticator =>
          env.eventBus.publish(LogoutEvent(identity, request, request2lang))
          val response = Ok(Json.toJson(Good(message = "logout completed")))
          env.authenticatorService.discard(authenticator, Future.successful(response /*.withCookies(Cookie("XSRF-TOKEN",authenticator.id))*/ ))
        }
      case None => Future.successful(Ok(Json.toJson(Good(message = "logout completed"))))
    }
  }

  /**
   * Create an User object
   */
  protected def createUser(loginInfo: LoginInfo, signUp: SignUp): User = {
    val fullName = signUp.fullName.getOrElse(signUp.firstName.getOrElse("None") + " " + signUp.lastName.getOrElse("None"))
    val info = BaseInfo(
      firstName = signUp.firstName,
      lastName = signUp.lastName,
      fullName = Some(fullName),
      gender = None)
    User(
      loginInfo = loginInfo,
      email = Some(signUp.identifier),
      username = None,
      avatarUrl = None,
      info = info)
  }

}