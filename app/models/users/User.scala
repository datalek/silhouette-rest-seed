package models.users

import com.mohiva.play.silhouette.core.Identity
import com.mohiva.play.silhouette.core.LoginInfo
import java.util.UUID

import models.authorizations._

/**
 * A user of this platform
 */
case class User(
  id: String = UUID.randomUUID.toString,
  loginInfo: LoginInfo,
  socials: Option[Seq[LoginInfo]] = None,
  email: Option[String],
  username: Option[String],
  avatarUrl: Option[String],
  info: BaseInfo,
  roles: Set[Role] = Set(SimpleUser)) extends Identity {

}

object User {

}
