package modules.cake

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.services.AuthInfoService
import com.mohiva.play.silhouette.api.util.PasswordHasher

/**
 * Provides the credentials provider.
 *
 * @param authInfoService The auth info service implemenetation.
 * @param passwordHasher The default password hasher implementation.
 */
trait CredentialsProviderModule {

  def authInfoService: AuthInfoService
  def passwordHasher: PasswordHasher

  lazy val credentialsProvider = new CredentialsProvider(authInfoService, passwordHasher, Seq(passwordHasher))

}