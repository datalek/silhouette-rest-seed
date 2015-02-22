package utils.responses.rest

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * An util class, represent a bad response, not good
 *
 * @param code of error
 * @param error an object that expone the errors
 */
class Bad(val code: Option[Int], val error: JsValue) {
  def status = "ko"
}

/**
 * Companion object for Good class
 */
object Bad {

  def apply(code: Option[Int] = None, message: String) = new Bad(code, JsString(message))
  def apply(code: Option[Int], message: JsValue) = new Bad(code, message)
  def apply(message: JsValue) = new Bad(None, message)
  def unapply(bad: Bad) = Some((bad.status, bad.code, bad.error))

  /**
   * Rest format
   */
  implicit val restFormat: Format[Bad] = {
    val reader: Reads[Bad] = (
      (__ \ "code").readNullable[Int] ~
      (__ \ "error").read[JsValue])(Bad.apply(_, _))

    val writer: Writes[Bad] = (
      (__ \ "status").write[String] ~
      (__ \ "code").writeNullable[Int] ~
      (__ \ "error").write[JsValue])(unlift(Bad.unapply _))

    Format(reader, writer)
  }

}