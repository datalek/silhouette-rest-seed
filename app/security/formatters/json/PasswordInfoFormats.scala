package security.formatters.json

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.mohiva.play.silhouette.api.util.PasswordInfo

object PasswordInfoFormats {

  implicit val restFormat = (
    (__ \ "hasher").format[String] ~
    (__ \ "password").format[String] ~
    (__ \ "salt").formatNullable[String])(PasswordInfo.apply, unlift(PasswordInfo.unapply))
}