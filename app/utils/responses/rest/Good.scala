package utils.responses.rest

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * An util class, represent a good response, it's all right
 * 
 * @param message
 */
class Good(val message: JsValue) {
  def status = "ok"
}

/**
 * Companion object for Good class
 */
object Good {

  def apply(message: String) = new Good(JsString(message))
  def apply(message: JsValue) = new Good(message)
  def unapply(good: Good) = Some((good.status, good.message))

  /**
   * Rest format
   */
  implicit val restFormat: Format[Good] = {
    /** because of single value of read, i have to do map, it's a bug of play's json library, but don't worry ;)*/
    val reads: Reads[Good] = (
      (__ \ "message").read[JsValue]).map(m => Good.apply(m))

    import play.api.libs.json.Writes._
    val writes: Writes[Good] = (
      (__ \ "status").write[String] ~
      (__ \ "message").write[JsValue])(unlift(Good.unapply _))

    Format(reads, writes)
  }

}
