package modules.cake

import com.mohiva.play.silhouette.impl.services._
import com.mohiva.play.silhouette.api.util.HTTPLayer

/**
 * Provides the avatar service.
 *
 * @param httpLayer The HTTP layer implementation.
 */
trait AvatarServiceModule {

  def httpLayer: HTTPLayer

  lazy val avatarService = new GravatarService(httpLayer)

}