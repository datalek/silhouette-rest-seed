package controllers

import play.api.libs.json._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import models.users.User
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.Silhouette
import modules.cake.HeaderEnvironmentModule
import models.authorizations._


import utils.responses.rest._

/**
 * The basic application controller.
 *
 * @param env The Silhouette environment.
 */
class RestApplicationController extends Silhouette[User, JWTAuthenticator] with HeaderEnvironmentModule {

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
  
  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def onlyGodOrUser = SecuredAction(WithRole(God) || WithRole(SimpleUser)).async { implicit request =>
    Future.successful(Ok(Json.obj("result" -> "Oh yess GOD")))
  }

}

object RestApplicationController extends RestApplicationController