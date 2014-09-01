package formatters

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.mohiva.play.silhouette.core.LoginInfo

import models.users.BaseInfo

/**
 * This object contains all format for User class
 */
object BaseInfoFormats {

  val storageFormat = (
    (__ \ "firstName").formatNullable[String] ~
    (__ \ "lastName").formatNullable[String] ~
    (__ \ "fullName").formatNullable[String] ~
    (__ \ "gender").formatNullable[String])(BaseInfo.apply _, unlift(BaseInfo.unapply _))

  val restFormat = storageFormat

}