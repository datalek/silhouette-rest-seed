package services

import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.mvc.RequestHeader
import play.api.i18n.Lang
import play.twirl.api.{ Txt, Html }
import com.typesafe.plugin._
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import akka.actor._
import com.mohiva.play.silhouette.api._

/**
 *
 */
trait MailService[I <: Identity] {

  /**
   *
   */
  val fromAddress = current.configuration.getString("smtp.from").get

  def sendWelcomeEmail(user: I)(implicit request: RequestHeader, lang: Lang) 

//  def sendPasswordResetEmail(user: I, token: String)(implicit request: RequestHeader, lang: Lang) 
//
//  def sendPasswordChangedNotice(user: I)(implicit request: RequestHeader, lang: Lang) 

  /**
   * @param subject of the email
   * @param recipient of the email
   * @param body pair with Text and Html email 
   */
  def sendEmail(subject: String, recipient: String, body: (Option[Txt], Option[Html])) = {

    play.Logger.debug(s"[securesocial] sending email to $recipient")
    play.Logger.debug(s"[securesocial] mail = [$body]")
    
    Akka.system.scheduler.scheduleOnce(1 seconds) {
      val mail = use[MailerPlugin].email
      mail.setSubject(subject)
      mail.setRecipient(recipient)
      mail.setFrom(fromAddress)
      // the mailer plugin handles null / empty string gracefully
      mail.send(body._1.map(_.body).getOrElse(""), body._2.map(_.body).getOrElse(""))
    }
  }

}
