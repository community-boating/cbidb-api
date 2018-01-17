package Logic.PreparedQueries
import java.sql.ResultSet

import Services.Authentication.{ApexUserType, UserType}

class GetJpTeams extends PreparedQuery[GetJpTeamsResult]{
  override val allowedUserTypes: Set[UserType] = Set(ApexUserType)
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
}

case class GetJpTeamsResult(
  teamId: Int,
  teamName: String,
  points: Int
)