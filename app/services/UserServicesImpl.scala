package services

import java.util.UUID
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.silhouette.core.LoginInfo
import com.mohiva.play.silhouette.core.services.AuthInfo
import com.mohiva.play.silhouette.core.providers.CommonSocialProfile
import scala.concurrent.Future
import scala.collection.mutable
import models.users._

/**
 * BASIC IMPLEMENTATION
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 */
class UserServiceImpl extends UserService {

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    Future.successful {
      UserServiceImpl.users.find {
        case (id, user) => user.loginInfo == loginInfo || user.socials.map(_.find(li => li == loginInfo)).isDefined
      }.map(_._2)
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    UserServiceImpl.users += (user.id -> user)
    Future.successful(user)
  }

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save[A <: AuthInfo](profile: CommonSocialProfile) = {
    retrieve(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        val u = user.copy(info = BaseInfo(
          firstName = profile.firstName,
          lastName = profile.lastName,
          fullName = profile.fullName,
          gender = None),
          email = profile.email,
          avatarUrl = profile.avatarURL)
        save(u)
      case None => // Insert a new user
        val u = User(
          loginInfo = profile.loginInfo,
          username = None,
          info = BaseInfo(
            firstName = profile.firstName,
            lastName = profile.lastName,
            fullName = profile.fullName,
            gender = None),
          email = profile.email,
          avatarUrl = profile.avatarURL)
        save(u)
    }
  }

  /**
   * Link a social social profile on a user.
   *
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def link[A <: AuthInfo](user: User, profile: CommonSocialProfile): Future[User] = {
    val s = user.socials.getOrElse(Seq())
    val u = user.copy(socials = Some(s :+ profile.loginInfo))
    save(u)
  }

}

object UserServiceImpl {
  val users: mutable.HashMap[String, User] = mutable.HashMap()
}