package models.users

/**
 * Base info of an user
 */
case class BaseInfo(
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  gender: Option[String]) {

}