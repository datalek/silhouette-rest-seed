package modules.cake

import play.api.Play
import play.api.Play.current
import com.mohiva.play.silhouette.impl.providers.oauth2._
import com.mohiva.play.silhouette.impl.providers.oauth1._
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.{ CookieSecretProvider, CookieSecretSettings }
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.IDGenerator
import com.mohiva.play.silhouette.api.util.ExtractableRequest
import com.mohiva.play.silhouette.impl.providers.{ OAuth1Settings, OAuth2Settings, OAuth1TokenSecretProvider }
import com.mohiva.play.silhouette.impl.providers.OAuth2StateProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{ CookieStateProvider, DummyStateProvider, CookieStateSettings }

/**
 * Provide all social providers
 */
trait SocialProviderModule {

  def cacheLayer: CacheLayer
  def httpLayer: HTTPLayer
  def idGenerator: IDGenerator

  /**
   * Provides the OAuth2 state provider.
   *
   * @param idGenerator The ID generator implementation.
   * @return The OAuth2 state provider implementation.
   */
  lazy val stateProvider: OAuth2StateProvider = new DummyStateProvider
  
  /**
   * Provides the OAuth1 token secret provider.
   *
   * @return The OAuth1 token secret provider implementation.
   */
  lazy val oAuth1TokenSecretProvider: OAuth1TokenSecretProvider = {
    new CookieSecretProvider(CookieSecretSettings(
      cookieName = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookieName").get,
      cookiePath = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookiePath").get,
      cookieDomain = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookieDomain"),
      secureCookie = Play.configuration.getBoolean("silhouette.oauth1TokenSecretProvider.secureCookie").get,
      httpOnlyCookie = Play.configuration.getBoolean("silhouette.oauth1TokenSecretProvider.httpOnlyCookie").get,
      expirationTime = Play.configuration.getInt("silhouette.oauth1TokenSecretProvider.expirationTime").get
    ), Clock())
  }
  
  /**
   * Provides the Facebook provider.
   *
   * @param cacheLayer The cache layer implementation.
   * @param httpLayer The HTTP layer implementation.
   * @return The Facebook provider.
   */
  lazy val facebookProvider = {
    FacebookProvider(httpLayer, stateProvider, OAuth2Settings(
      authorizationURL = Play.configuration.getString("silhouette.facebook.authorizationURL"),
      accessTokenURL = Play.configuration.getString("silhouette.facebook.accessTokenURL").get,
      redirectURL = Play.configuration.getString("silhouette.facebook.redirectURL").get,
      clientID = Play.configuration.getString("silhouette.facebook.clientID").get,
      clientSecret = Play.configuration.getString("silhouette.facebook.clientSecret").get,
      scope = Play.configuration.getString("silhouette.facebook.scope")))
  }

  /**
   * Provides the Google provider.
   *
   * @param cacheLayer The cache layer implementation.
   * @param httpLayer The HTTP layer implementation.
   * @return The Google provider.
   */
  lazy val provideGoogleProvider = {
    GoogleProvider(httpLayer, stateProvider, OAuth2Settings(
      authorizationURL = Play.configuration.getString("silhouette.google.authorizationURL"),
      accessTokenURL = Play.configuration.getString("silhouette.google.accessTokenURL").get,
      redirectURL = Play.configuration.getString("silhouette.google.redirectURL").get,
      clientID = Play.configuration.getString("silhouette.google.clientID").get,
      clientSecret = Play.configuration.getString("silhouette.google.clientSecret").get,
      scope = Play.configuration.getString("silhouette.google.scope")))
  }

  /**
   * Provides the Twitter provider.
   *
   * @param cacheLayer The cache layer implementation.
   * @param httpLayer The HTTP layer implementation.
   * @return The Twitter provider.
   */
  lazy val provideTwitterProvider = {
    val settings = OAuth1Settings(
      requestTokenURL = Play.configuration.getString("silhouette.twitter.requestTokenURL").get,
      accessTokenURL = Play.configuration.getString("silhouette.twitter.accessTokenURL").get,
      authorizationURL = Play.configuration.getString("silhouette.twitter.authorizationURL").get,
      callbackURL = Play.configuration.getString("silhouette.twitter.callbackURL").get,
      consumerKey = Play.configuration.getString("silhouette.twitter.consumerKey").get,
      consumerSecret = Play.configuration.getString("silhouette.twitter.consumerSecret").get)

    TwitterProvider(httpLayer, new PlayOAuth1Service(settings), oAuth1TokenSecretProvider, settings)
  }

}