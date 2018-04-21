package Services.Logger

import Services.Emailer.Emailer
import Services.PermissionsAuthority

class ProductionLogger private[Services](emailer: Emailer)  extends Logger {
  def trace(s: String): Unit = println(s)
  def trace(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

  def info(s: String): Unit = println(s)
  def info(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

  def warning(s: String): Unit = {
    emailer.send("CBI API Warning" + getSubjectSuffix, s)
    println(s)
  }
  def warning(s: String, e: Throwable): Unit = {
    val msg = s + "\n" + prettyPrintException(e)
    emailer.send("CBI API Warning" + getSubjectSuffix, msg)
    println(msg)
  }

  def error(s: String): Unit = {
    emailer.send("CBI API Error" + getSubjectSuffix, s)
    println(s)
  }
  def error(s: String, e: Throwable): Unit = {
    val msg = s + "\n" + prettyPrintException(e)
    emailer.send("CBI API Error" + getSubjectSuffix, msg)
    println(msg)
  }

  private def getRandomString(numberLetters: Int = 5): String = scala.util.Random.alphanumeric.take(numberLetters).mkString

  private def getSubjectSuffix: String = " (" + getRandomString() + ") (" + PermissionsAuthority.instanceName.get + ")"
}