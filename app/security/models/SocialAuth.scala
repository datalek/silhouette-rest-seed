package security.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.mohiva.play.silhouette.api.util.Credentials

/**
 * Generic class for Rest Social authentication
 */
case class SocialAuth(
  token: String,
  expiresIn: Option[Int],
  secret: Option[String])

/**
 * Companion object
 */
object SocialAuth {
  implicit val restFormat = SocialAuthFormat.restFormat
}

/**
 * Formatter for SocialAuth class
 */
object SocialAuthFormat {

  implicit val restFormat = (
    (__ \ "accessToken").format[String] ~
    (__ \ "expiresIn").formatNullable[Int] ~
    (__ \ "secret").formatNullable[String])(SocialAuth.apply, unlift(SocialAuth.unapply))

}