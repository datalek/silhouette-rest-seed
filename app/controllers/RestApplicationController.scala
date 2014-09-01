package controllers

import models.users.User
import com.mohiva.play.silhouette.core.{ LogoutEvent, Environment, Silhouette }
import com.mohiva.play.silhouette.contrib.authenticators.HeaderAuthenticator
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import javax.inject.Inject

import utils.responses.rest._

/**
 * The basic application controller.
 *
 * @param env The Silhouette environment.
 */
class RestApplicationController @Inject() (implicit val env: Environment[User, HeaderAuthenticator])
  extends Silhouette[User, HeaderAuthenticator] {

  implicit val userFormat = formatters.json.UserFormats.restFormat

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Ok(Json.toJson(user)))
      case None => Future.successful(Ok(Json.toJson(Good(message = "you are not logged! Login man!"))))
    }
  }

}
