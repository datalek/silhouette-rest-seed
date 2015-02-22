package modules.cake

import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticator, JWTAuthenticator }
import com.mohiva.play.silhouette.impl.util.BCryptPasswordHasher
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.util.PlayHTTPLayer
import modules.cake.{ HeaderAuthenticatorServiceModule/*, CookieAuthenticatorServiceModule */}
import models.users.User
import models.daos._


/**
 * Provides the Silhouette environment.
 *
 * @param userService The user service implementation.
 * @param authenticatorService The authentication service implementation.
 * @param eventBus The event bus instance.
 */
trait HeaderEnvironmentModule
  extends HeaderAuthenticatorServiceModule
  with UserServiceModule
  with AuthInfoServiceModule
  with CredentialsProviderModule 
  with SocialProviderModule 
  /*with MailServiceModule*/ {

  /**
   * Configures the module.
   */
  lazy val cacheLayer = new PlayCacheLayer
  lazy val httpLayer = new PlayHTTPLayer
  lazy val eventBus = EventBus()
  lazy val idGenerator = new SecureRandomIDGenerator
  lazy val passwordInfoDAO = new PasswordInfoDAO
  lazy val oauth1InfoDAO = new OAuth1InfoDAO
  lazy val oauth2InfoDAO = new OAuth2InfoDAO
  lazy val passwordHasher = new BCryptPasswordHasher

  implicit lazy val env: Environment[User, JWTAuthenticator] = {
    Environment[User, JWTAuthenticator](
      userService,
      authenticatorService,
      Map(
        credentialsProvider.id -> credentialsProvider,
        facebookProvider.id -> facebookProvider),
      eventBus)
  }

}