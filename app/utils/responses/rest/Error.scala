package utils.responses.rest

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.http.Status

/**
 * An util class, represent an error response, like 404 or others
 */
case class Error(
  val status: Int = Status.INTERNAL_SERVER_ERROR,
  val errorCode: Int = 10000,
  val field: String = "",
  val message: String = "Error performing operation",
  val developerMessage: String = "Error performing operation")

/**
 * Companion object for Error class
 */
object Error {

  /* 
  def apply(message: String) = {
    Error(status = Status.BAD_REQUEST, message = message)
  }

  def apply(field: String, message: String) = {
    Error(status = Status.BAD_REQUEST, field = field, message = message)
  }

  def apply(field: String, message: String, developerMessage: String) = {
    Error(status = Status.BAD_REQUEST, field = field, message = message,
      developerMessage = developerMessage)
  }
  */

  /**
   * Auto generated format
   */
  implicit val restFormat = Json.format[Error]

}