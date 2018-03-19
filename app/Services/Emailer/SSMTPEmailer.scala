package Services.Emailer

import Services.PermissionsAuthority
import Services.Shell.ShellManager

import sys.process._

class SSMTPEmailer(alwaysSendTo: Option[String]) extends Emailer {
  private def sanitize(s: String): String = s.replace("\"", "\\\"").replace("'", "\\'")
  def send(to: String, subject: String, body: String): Unit = {
    val sendTo = alwaysSendTo match {
      case Some(s) => s
      case None => to
    }

    val command = "ssmtp " + sendTo + " -F CBI-API"
    val stdin = "Subject: " + subject + "\n\n" + body
    println(command)
    ShellManager.execute(command, None, Some(20000), None, Some(stdin))
  }
}
