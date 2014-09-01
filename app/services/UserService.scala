package services

import com.mohiva.play.silhouette.core.services.{ AuthInfo, IdentityService }
import com.mohiva.play.silhouette.core.providers.CommonSocialProfile
import scala.concurrent.Future

import models.users.User

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save[A <: AuthInfo](profile: CommonSocialProfile): Future[User]

  /**
   * Link a social social profile on a user.
   *
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def link[A <: AuthInfo](user: User, profile: CommonSocialProfile): Future[User]
}