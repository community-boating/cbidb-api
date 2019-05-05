package Services

import java.sql.ResultSet

class ResultSetWithOption(rs: ResultSet) {
  def getOptionString(c: Int): Option[String] = {
    val ret = rs.getString(c)
    if (rs.wasNull()) None
    else Some(ret)
  }
  def getStringOrEmptyString(c: Int): String = {
    getOptionString(c) match {
      case Some(s) => s
      case None => ""
    }
  }
  def getOptionInt(c: Int): Option[Int] = {
    val ret = rs.getInt(c)
    if (rs.wasNull()) None
    else Some(ret)
  }
}

object ResultSetWithOption {
  implicit def wrapResultSet(rs: ResultSet): ResultSetWithOption = new ResultSetWithOption(rs)
}