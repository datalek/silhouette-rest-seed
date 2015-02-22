package services

import scala.concurrent.Future
import play.api.libs.json.{JsValue, JsNull}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.{ AuthInfo, IdentityService }
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import security.models.SignUp
import models.users.User

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

  /**
   * Create a user from login information and signup information
   *
   * @param loginInfo The information about login
   * @param signUp The information about User
   * @param avatarUrl string with url to avatar image
   * @param json all json with signup information
   */
  def create(loginInfo: LoginInfo, signUp: SignUp, avatarUrl: Option[String] = None, json: JsValue = JsNull): Future[User]

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