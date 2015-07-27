package controllers.security.rest

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import modules.cake.{ HeaderEnvironmentModule, AvatarServiceModule }
import scala.concurrent.Future
import utils.responses.rest._
import security.models._
import models.users._

/**
 * This controller manage registration of an user
 */
class RestSignUpController extends Silhouette[User, JWTAuthenticator]
  with HeaderEnvironmentModule
  with AvatarServiceModule {

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
   * 		  "password": "",
   * 		  "identifier": "",
   *  		"firstName": "",
   *    	"lastName": "",
   *     	"fullName": ""
   * 	}
   *
   * @return The result to display.
   */
  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp].map { signUp =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
      (userService.retrieve(loginInfo).flatMap {
        case None => /* user not already exists */
          val authInfo = passwordHasher.hash(signUp.password)
          for {
            avatar <- avatarService.retrieveURL(signUp.identifier)
            userToSave <- userService.create(loginInfo, signUp, avatar)
            user <- userService.save(userToSave)
            authInfo <- authInfoService.save(loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(loginInfo)
            token <- env.authenticatorService.init(authenticator)
            result <- env.authenticatorService.embed(token, Future.successful {
              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
            })
          } yield {
            env.eventBus.publish(SignUpEvent(user, request, request2lang))
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            mailService.sendWelcomeEmail(user)
            result
          }
        case Some(u) => /* user already exists! */
          Future.successful(Conflict(Json.toJson(Bad(message = "user already exists"))))
      })
    }.recoverTotal {
      case error =>
        Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toFlatJson(error)))))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2lang))
    request.authenticator.discard(Future.successful(Ok))
  }
}

object RestSignUpController extends RestSignUpController