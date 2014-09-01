package controllers.security.rest

import javax.inject.Inject
import utils.responses.rest._
import services.UserService
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.core._
import com.mohiva.play.silhouette.core.providers._
import com.mohiva.play.silhouette.core.exceptions._
import com.mohiva.play.silhouette.core.services.AuthInfoService
import com.mohiva.play.silhouette.contrib.authenticators.HeaderAuthenticator

import scala.concurrent.{ Future }


import security.models._
import models.users.User

/**
 * This controller manage authentication of an user by identifier and password
 */
class RestCredentialsAuthController @Inject() (
  implicit val env: Environment[User, HeaderAuthenticator],
  val userService: UserService,
  val authInfoService: AuthInfoService) extends Silhouette[User, HeaderAuthenticator] {

  /**
   *
   */
  implicit val restCredentialFormat = security.formatters.json.CredentialFormat.restFormat

  /**
   * Authenticates a user against the credentials provider.
   *
   * receive json like this:
   * {
   * 	"identifier": "...",
   *  	"password": "..."
   * }
   *
   * @return The result to display.
   */
  def authenticate = Action.async(parse.json) { implicit request =>
    request.body.validate[Credentials] match {
      case JsSuccess(credentials, _) =>
        (env.providers.get(CredentialsProvider.Credentials) match {
          case Some(p: CredentialsProvider) => p.authenticate(credentials)
          case _ => Future.failed(new AuthenticationException(s"Cannot find credentials provider"))
        }).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) => env.authenticatorService.create(user).flatMap { authenticator =>
              env.eventBus.publish(LoginEvent(user, request, request2lang))
              val response = Ok(Json.toJson(Token(token = authenticator.id, expiresOn = authenticator.expirationDate)))
              env.authenticatorService.init(authenticator, Future.successful(response/*.withCookies(Cookie("XSRF-TOKEN",authenticator.id))*/))
            }
            case None =>
              Future.failed(new AuthenticationException("Couldn't find user"))
          }
        }.recoverWith(exceptionHandler)
      case JsError(e) => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(e))))
    }
  }

}