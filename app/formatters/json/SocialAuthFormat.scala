package formatters.json

import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.mohiva.play.silhouette.core.providers.Credentials

/**
 * Generic class for Rest Social authentication
 */
case class SocialAuth(
  token: String,
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
    (__ \ "token").format[String] ~
    (__ \ "secret").formatNullable[String])(SocialAuth.apply, unlift(SocialAuth.unapply))

}