import play.api.i18n.{ Messages, Lang }
import play.api.mvc._
import play.api.mvc.{ Result, RequestHeader }
import play.api.mvc.Rendering
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import play.api.GlobalSettings
import play.api.libs.json._
import com.mohiva.play.silhouette.api.{ Logger, SecuredSettings }
import scala.concurrent.Future

/**
 * The global configuration.
 */
object Global extends GlobalSettings with SecuredSettings with Logger {

  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @param lang The currently selected language.
   * @return The result to send to the client.
   */
  //  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
  //    controllers.StaticResponse.onNotAuthenticated(request, lang)
  //  }

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @param lang The currently selected language.
   * @return The result to send to the client.
   */
  //  override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
  //    controllers.StaticResponse.onNotAuthorized(request, lang)
  //  }
}
