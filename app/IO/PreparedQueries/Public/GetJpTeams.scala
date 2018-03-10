package IO.PreparedQueries.Public

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryCastableToJSObject
import Services.Authentication.{PublicUserType, UserType}
import play.api.libs.json.{JsArray, JsString}

class GetJpTeams extends PreparedQueryCastableToJSObject[GetJpTeamsResult]{
  override val allowedUserTypes: Set[UserType] = Set(PublicUserType)
  val getQuery: String =
    """
      |select t.team_id, t.team_name, sum(points)
      |from jp_teams t, jp_team_event_points tep
      |where t.team_id = tep.team_id
      |group by t.team_id, t.team_name
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): GetJpTeamsResult = GetJpTeamsResult(
    rs.getInt(1),
    rs.getString(2),
    rs.getInt(3)
  )

  val columnNames = List(
    "TEAM_ID",
    "TEAM_NAME",
    "POINTS"
  )

  def mapCaseObjectToJsArray(caseObject: GetJpTeamsResult): JsArray = JsArray(IndexedSeq(
    JsString(caseObject.teamName),
    JsString(caseObject.points.toString)
  ))
}

case class GetJpTeamsResult(
  teamId: Int,
  teamName: String,
  points: Int
)