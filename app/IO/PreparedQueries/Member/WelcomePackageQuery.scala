package IO.PreparedQueries.Member

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.MemberUserType

// TODO: replace with entity-based arch
class WelcomePackageQuery(userName: String) extends PreparedQueryForSelect[WelcomePackageResult](allowedUserTypes = Set(MemberUserType)) {
  override def mapResultSetRowToCaseObject(rs: ResultSet): WelcomePackageResult = WelcomePackageResult(rs.getString(1), rs.getInt(2))

  override def getQuery: String = "select email, person_id from persons where pw_hash is not null and lower(email) = ?"

  override val params: List[String] = List(userName.toLowerCase)
}

case class WelcomePackageResult(
  email: String,
  personId: Int
)