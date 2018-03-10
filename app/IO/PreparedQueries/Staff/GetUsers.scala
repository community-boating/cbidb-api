package IO.PreparedQueries.Staff

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryForSelectCastableToJSObject
import Services.Authentication.StaffUserType
import play.api.libs.json.{JsArray, JsString}

class GetUsers extends PreparedQueryForSelectCastableToJSObject[GetUsersResult](Set(StaffUserType)) {
  val getQuery: String =
    """
      |select user_id, user_name from users
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): GetUsersResult = GetUsersResult(
    rs.getInt(1),
    rs.getString(2),
  )

  val columnNames = List(
    "USER_ID",
    "USER_NAME"
  )

  def mapCaseObjectToJsArray(caseObject: GetUsersResult): JsArray = JsArray(IndexedSeq(
    JsString(caseObject.userId.toString),
    JsString(caseObject.userName)
  ))
}

case class GetUsersResult(
  userId: Int,
  userName: String,
)