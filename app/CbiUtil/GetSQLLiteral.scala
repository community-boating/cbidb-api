package CbiUtil

object GetSQLLiteral {
  def apply(s: String): String = "'" + s + "'"
  def apply(s: Option[String]): String = s match { case Some(x) => apply(x); case None => "null" }
  def apply(i: Int): String = i.toString
  def apply(b: Boolean): String = if (b) "'Y'" else "'N'"
}
