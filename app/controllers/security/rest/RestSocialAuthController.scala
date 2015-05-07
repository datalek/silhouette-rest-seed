package controllers.security.rest

import utils.responses.rest._
import services.UserService
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.exceptions.ConfigurationException
import com.mohiva.play.silhouette.api.services.AuthInfoService
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import modules.cake.{ HeaderEnvironmentModule, AvatarServiceModule }

import scala.concurrent.{ Future, Promise }

import security.models._
import models.users.User

/**
 * This controller manage authentication of an user by social service (like facebook and other)
 */
class RestSocialAuthController extends Silhouette[User, JWTAuthenticator] with HeaderEnvironmentModule {

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticate(provider: String) = Action.async(parse.json) { implicit request =>
    (env.providers.get(provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoService.save(profile.loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(user.loginInfo)
            token <- env.authenticatorService.init(authenticator)
            result <- env.authenticatorService.embed(token, Future.successful {
              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
            })
          } yield {
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            result
          }
        }
      case _ => Future.failed(new ConfigurationException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recoverWith(exceptionHandler)
  }

  /**
   * Link social with a existing user.
   *
   * receive json like this:
   * {
   * 	"accessToken": "...",
   *  	"expiresIn": 0000, //optional
   *  	"secret": "..."  //this is for OAuth1, for OAuth2 isn't request
   * }
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def link(provider: String) = SecuredAction.async(parse.json) { implicit request =>
    request.body.validate[SocialAuth].map { socialAuth =>
      (profileAndAuthInfo(provider, socialAuth).flatMap {
        case (profile: CommonSocialProfile, authInfo: AuthInfo) =>
          for {
            user <- userService.link(request.identity, profile)
            authInfo <- authInfoService.save(profile.loginInfo, authInfo)
          } yield {
            Ok(Json.toJson(Good(message = "link with social completed!")))
          }
      }).recoverWith(exceptionHandler)
    }.recoverTotal {
      case error => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(error))))
    }
  }

  /**
   * Util method to use for retrieve information from authInfo
   *
   * @param provider where retrieve information
   * @param socialAuth object where get auth information
   * @return a pair with CommonSocialProfile and AuthInfo
   */
  protected def profileAndAuthInfo(provider: String, socialAuth: SocialAuth) = {
    env.providers.get(provider) match {
      case Some(p: OAuth1Provider with CommonSocialProfileBuilder) => //for OAuth1 provider type
        val authInfo = OAuth1Info(token = socialAuth.token, socialAuth.secret.get)
        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
      case Some(p: OAuth2Provider with CommonSocialProfileBuilder) => //for OAuth2 provider type
        val authInfo = OAuth2Info(accessToken = socialAuth.token, expiresIn = socialAuth.expiresIn)
        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
      case _ => Future.successful(new ConfigurationException(s"Cannot retrive information with unexpected social provider $provider"))
    }
  }

}

object RestSocialAuthController extends RestSocialAuthController