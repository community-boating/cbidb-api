package Services.Logger

import java.io.{PrintWriter, StringWriter}

abstract class Logger {
  def trace(s: String): Unit
  def trace(e: Exception): Unit

  def info(s: String): Unit
  def info(e: Exception): Unit

  def warning(s: String): Unit
  def warning(e: Exception): Unit

  def error(s: String): Unit
  def error(e: Exception): Unit

  def prettyPrintException(e: Exception): String = {
    val sw = new StringWriter
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
