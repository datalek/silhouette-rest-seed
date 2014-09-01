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
import com.mohiva.play.silhouette.core.services.{ AuthInfoService, AuthInfo }
import com.mohiva.play.silhouette.contrib.authenticators.HeaderAuthenticator

import scala.concurrent.{ Future, Promise }

import security.models._
import models.users.User

/**
 * This controller manage authentication of an user by social service (like facebook and other)
 */
class RestSocialAuthController @Inject() (
  implicit val env: Environment[User, HeaderAuthenticator],
  val userService: UserService,
  val authInfoService: AuthInfoService) extends Silhouette[User, HeaderAuthenticator] {

  /**
   * Util method to use for retrieve information from authInfo
   *
   * @param provider where retrieve information
   * @param socialAuth object where get auth information
   * @return a pair with CommonSocialProfile and AuthInfo
   */
  protected def profileAndAuthInfo(provider: String, socialAuth: SocialAuth) = {
    env.providers.get(provider) match {
      case Some(p: OAuth1Provider with CommonSocialProfileBuilder[_]) => //for OAuth1 provider type
        val authInfo = OAuth1Info(token = socialAuth.token, socialAuth.secret.get)
        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
      case Some(p: OAuth2Provider with CommonSocialProfileBuilder[_]) => //for OAuth2 provider type
        val authInfo = OAuth2Info(accessToken = socialAuth.token, expiresIn = socialAuth.expiresIn)
        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
      case _ => Future.successful(new AuthenticationException(s"Cannot retrive information with unexpected social provider $provider"))
    }
  }

  /**
   * Authenticates a user against a social provider.
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
  def authenticate(provider: String) = Action.async(parse.json) { implicit request =>
    request.body.validate[SocialAuth] match {
      case JsSuccess(socialAuth, _) =>
        (profileAndAuthInfo(provider, socialAuth).flatMap {
          case (profile: CommonSocialProfile, authInfo: AuthInfo) =>
            (for {
              user <- userService.save(profile)
              authInfo <- authInfoService.save(profile.loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(user)
            } yield {
              env.eventBus.publish(LoginEvent(user, request, request2lang))
              val response = Ok(Json.toJson(Token(token = authenticator.id, expiresOn = authenticator.expirationDate)))
              env.authenticatorService.init(authenticator, Future.successful(response))
            }).flatMap(r => r)
        }).recoverWith(exceptionHandler)
      case JsError(e) => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(e))))
    }
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
    request.body.validate[SocialAuth] match {
      case JsSuccess(socialAuth, _) =>
        (profileAndAuthInfo(provider, socialAuth).flatMap {
          case (profile: CommonSocialProfile, authInfo: AuthInfo) =>
            (for {
              user <- userService.link(request.identity, profile)
              authInfo <- authInfoService.save(profile.loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(user)
            } yield {
              env.eventBus.publish(LoginEvent(user, request, request2lang))
              val response = Ok(Json.toJson(Token(token = authenticator.id, expiresOn = authenticator.expirationDate)))
              env.authenticatorService.init(authenticator, Future.successful(response))
            }).flatMap(r => r)
        }).recoverWith(exceptionHandler)
      case JsError(e) => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(e))))
    }
  }

}