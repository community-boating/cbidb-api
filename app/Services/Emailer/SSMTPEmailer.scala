package Services.Emailer

import sys.process._

class SSMTPEmailer(alwaysSendTo: Option[String]) extends Emailer {
  private def sanitize(s: String): String = s.replace("\"", "\\\"").replace("'", "\\'")
  def send(to: String, subject: String, body: String): Unit = {
    val sendTo = alwaysSendTo match {
      case Some(s) => s
      case None => to
    }
    val commandInput = List(
      "{",
      "echo \"Subject:\"" + sanitize(subject) + ";"
    ) ++
      body.split("\n").toList.map(l => "echo \"" + sanitize(l) + "\";") ++
      List("}")

    val command = "'" + commandInput.mkString("") + "#| ssmtp " + sendTo + " -F CBI-API'"
    println(command)
    val result = command!


    println(result)
  }
}
