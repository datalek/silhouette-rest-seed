package security.formatters.json

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.mohiva.play.silhouette.core.providers.OAuth2Info

object OAuth2InfoFormats {

  implicit val restFormat = (
    (__ \ "accessToken").format[String] ~
    (__ \ "tokenType").formatNullable[String] ~
    (__ \ "expiresIn").formatNullable[Int] ~
    (__ \ "refreshToken").formatNullable[String])(OAuth2Info.apply, unlift(OAuth2Info.unapply))

}