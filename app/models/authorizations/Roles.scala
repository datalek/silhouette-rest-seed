package models.authorizations

import com.mohiva.play.silhouette.api.Authorization
import models.users._
import play.api.i18n._
import play.api.mvc.RequestHeader

/**
 * Check for authorization
 */
case class WithRole(role: Role) extends Authorization[User] {
  def isAuthorized(user: User)(implicit request: RequestHeader, lang: Lang) = user.roles match {
    case list: Set[Role] => list.contains(role)
    case _               => false
  }

}
/**
 * Trait for all roles
 */
trait Role {
  def name: String
}

/**
 * Companion object
 */
object Role {

  def apply(role: String): Role = role match {
    case God.name        => God
    case Admin.name      => Admin
    case SimpleUser.name => SimpleUser
    case _               => Unknown
  }

  def unapply(role: Role): Option[String] = Some(role.name)

}

/**
 * Administration role
 */
object God extends Role {
  val name = "god"
}

/**
 * Administration role
 */
object Admin extends Role {
  val name = "admin"
}

/**
 * Normal user role
 */
object SimpleUser extends Role {
  val name = "user"
}

/**
 * The generic unknown role
 */
object Unknown extends Role {
  val name = "-"
}