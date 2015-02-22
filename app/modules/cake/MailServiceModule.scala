//package modules.cake
//
//import play.api.mvc.RequestHeader
//import play.api.i18n.Lang
//import com.merle.play.auth.services._
//import com.merle.play.auth.users.GenericUser
//
///**
// * Provides the avatar service.
// *
// * @param httpLayer The HTTP layer implementation.
// */
//trait MailServiceModule {
//
//  class CustomMailTemplateService extends MailTemplateService {
//    /**
//     *
//     */
//    def welcomeEmailTemplate(user: GenericUser)(implicit request: RequestHeader, lang: Lang) = {
//      views.html.authentication.mails.welcomeEmail(user)(request, lang)
//    }
//    /**
//     *
//     */
//    def passwordResetEmailTemplate(user: GenericUser, token: String)(implicit request: RequestHeader, lang: Lang) = {
//      views.html.authentication.mails.passwordResetEmail(user, token)(request, lang)
//    }
//    /**
//     *
//     */
//    def passwordChangedNoticeTemplate(user: GenericUser)(implicit request: RequestHeader, lang: Lang) = {
//      views.html.authentication.mails.passwordChangedNotice(user)(request, lang)
//    }
//    /**
//     *
//     */
//    def invitationTemplate(toInvite: GenericUser, from: GenericUser, token: String)(implicit request: RequestHeader, lang: Lang) = {
//      views.html.authentication.mails.invitation(toInvite, from, token)(request, lang)
//    }
//  }
//
//  class CustomMailService extends MailService {
//
//    lazy val mailTemplateService = new CustomMailTemplateService
//
//    def sendInvitationEmail[I <: GenericUser](toInvite: I, from: I, token: String)(implicit request: RequestHeader, lang: Lang) = {
//      val txtAndHtml = (None, Some(mailTemplateService.invitationTemplate(toInvite, from, token)(request, lang)))
//      sendEmail("Reset password", toInvite.email.get, txtAndHtml)
//    }
//
//  }
//
//  lazy val mailService = new CustomMailService
//
//}