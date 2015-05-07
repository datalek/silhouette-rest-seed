package modules.cake

import play.api.Play
import play.api.Play.current
/** mohiva module import */
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.IDGenerator

/**
 * Provides the Header authenticator service.
 *
 * @param cacheLayer The cache layer implementation.
 * @param idGenerator The ID generator used to create the authenticator ID.
 */
trait HeaderAuthenticatorServiceModule {

  def idGenerator: IDGenerator

  lazy val authenticatorService: AuthenticatorService[JWTAuthenticator] = {
    val settings = JWTAuthenticatorSettings(
      headerName = Play.configuration.getString("silhouette.authenticator.headerName").getOrElse { "X-Auth-Token" },
      issuerClaim = Play.configuration.getString("silhouette.authenticator.issueClaim").getOrElse { "play-silhouette" },
      encryptSubject = Play.configuration.getBoolean("silhouette.authenticator.encryptSubject").getOrElse { true },
      authenticatorIdleTimeout = Play.configuration.getInt("silhouette.authenticator.authenticatorIdleTimeout"), // This feature is disabled by default to prevent the generation of a new JWT on every request
      authenticatorExpiry = Play.configuration.getInt("silhouette.authenticator.authenticatorExpiry").getOrElse { 12 * 60 * 60 },
      sharedSecret = Play.configuration.getString("application.secret").get)
    new JWTAuthenticatorService(
      settings = settings,
      dao = None,
      idGenerator = idGenerator,
      clock = Clock())
  }

}