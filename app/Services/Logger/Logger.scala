package Services.Logger

import java.io.{PrintWriter, StringWriter}

abstract class Logger {
  def trace(s: String): Unit
  def trace(s: String, e: Throwable): Unit

  def info(s: String): Unit
  def info(s: String, e: Throwable): Unit

  def warning(s: String): Unit
  def warning(s: String, e: Throwable): Unit

  def error(s: String): Unit
  def error(s: String, e: Throwable): Unit

  def prettyPrintException(e: Throwable): String = {
    val sw = new StringWriter
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
