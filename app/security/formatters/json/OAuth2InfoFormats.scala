package security.formatters.json

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.JsValueWrapper
import com.mohiva.play.silhouette.impl.providers.OAuth2Info

object OAuth2InfoFormats {

  implicit val restFormat = (
    (__ \ "accessToken").format[String] ~
    (__ \ "tokenType").formatNullable[String] ~
    (__ \ "expiresIn").formatNullable[Int] ~
    (__ \ "refreshToken").formatNullable[String] ~
    (__ \ "params").formatNullable[Map[String, String]])(OAuth2Info.apply, unlift(OAuth2Info.unapply))
}