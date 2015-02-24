package modules.cake

import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.services.DelegableAuthInfoService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import com.mohiva.play.silhouette.impl.providers.OAuth2Info

/**
 * Provides the auth info service.
 *
 * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
 * @param oauth1InfoDAO The implementation of the delegable OAuth1 auth info DAO.
 * @param oauth2InfoDAO The implementation of the delegable OAuth2 auth info DAO.
 */
trait AuthInfoServiceModule {

  def passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
  def oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info]
  def oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]

  lazy val authInfoService = new DelegableAuthInfoService(passwordInfoDAO, oauth1InfoDAO, oauth2InfoDAO)

}