package Services.Logger

class ProductionLogger extends Logger {
  def trace(s: String): Unit = println(s)
  def trace(e: Exception): Unit = println(prettyPrintException(e))

  def info(s: String): Unit = println(s)
  def info(e: Exception): Unit = println(prettyPrintException(e))

  def warning(s: String): Unit = println(s)
  def warning(e: Exception): Unit = println(prettyPrintException(e))

  def error(s: String): Unit = println(s)
  def error(e: Exception): Unit = println(prettyPrintException(e))
}
