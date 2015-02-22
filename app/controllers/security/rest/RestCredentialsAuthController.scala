package controllers.security.rest

import utils.responses.rest._
import services.UserService
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.AuthenticationException
import com.mohiva.play.silhouette.api.services.AuthInfoService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.util.Credentials
import modules.cake.HeaderEnvironmentModule

import scala.concurrent.{ Future }

import security.models._
import models.users.User

/**
 * This controller manage authentication of an user by identifier and password
 */
class RestCredentialsAuthController extends Silhouette[User, JWTAuthenticator]
  with HeaderEnvironmentModule {

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
    request.body.validate[Credentials].map { credentials =>
      (env.providers.get(CredentialsProvider.ID) match {
        case Some(p: CredentialsProvider) => p.authenticate(credentials)
        case _                            => Future.failed(new AuthenticationException(s"Cannot find credentials provider"))
      }).flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(user.loginInfo).flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            env.authenticatorService.init(authenticator).flatMap { token =>
              Future.successful(Ok(Json.obj("token" -> token)))
            }
          }
          case None =>
            Future.failed(new AuthenticationException("Couldn't find user"))
        }
      }.recoverWith(exceptionHandler)
    }.recoverTotal {
      case error => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(error))))
    }
  }

}

object RestCredentialsAuthController extends RestCredentialsAuthController