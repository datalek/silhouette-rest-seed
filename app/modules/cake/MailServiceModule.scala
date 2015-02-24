package modules.cake

import play.api.mvc.RequestHeader
import play.api.i18n.Lang
import services._
import models.users._

/**
 * Provides the mail service.
 *
 */
trait MailServiceModule {

  class SimpleMailService extends MailService[User] {

    def sendWelcomeEmail(user: User)(implicit request: RequestHeader, lang: Lang) = {
      val html = views.html.authentication.mails.welcomeEmail(user)(request, lang)
      val txtAndHtml = (None, Some(html))
      sendEmail("Welcome!!!!", user.email.get, txtAndHtml)
    }

  }

  lazy val mailService = new SimpleMailService

}