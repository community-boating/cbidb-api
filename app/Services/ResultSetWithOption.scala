package Services

import java.sql.ResultSet

class ResultSetWithOption(rs: ResultSet) {
  def getOptionString(c: Int): Option[String] = {
    val ret = rs.getString(c)
    if (rs.wasNull()) None
    else Some(ret)
  }
}

object ResultSetWithOption {
  implicit def wrapResultSet(rs: ResultSet): ResultSetWithOption = new ResultSetWithOption(rs)
}