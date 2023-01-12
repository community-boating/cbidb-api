package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDhGlobals @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			val q = new PreparedQueryForSelect[(String, LocalDateTime)](Set(StaffRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, LocalDateTime) = (rsw.getString(1), rsw.getLocalDateTime(2))

				override def getQuery: String =
					"""
					  |select flag, change_datetime from flag_changes where change_datetime >= (util_pkg.get_sysdate - 2) order by change_datetime desc
					""".stripMargin
			}

			val flagChanges = rc.executePreparedQueryForSelect(q)

			implicit val format = DhGlobalsShape.format
			val ret = DhGlobalsShape(
				serverDateTime = LocalDateTime.now,
				sunsetTime = LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).plusHours(20),
				windSpeedAvg = 12.3,
				winDir = "NNE",
				announcements = List(
					AnnouncementShape(message = "Sailing is pretty cool", priority = "H"),
					AnnouncementShape(message = "Kayaking kinda sucks", priority = "M"),
					AnnouncementShape(message = "AP > JP", priority = "L")
				),
				flagChanges = flagChanges.map(t => FlagChangeShape(flag = t._1, changeDatetime = t._2))
			)
			Future(Ok(Json.toJson(ret)))
		})
	})

	case class AnnouncementShape(
		message: String,
		priority: String
	)

	case class FlagChangeShape(
		flag: String,
		changeDatetime: LocalDateTime,
	)

	case class DhGlobalsShape(
		serverDateTime: LocalDateTime,
		sunsetTime: LocalDateTime,
		windSpeedAvg: Double,
		winDir: String,
		announcements: List[AnnouncementShape],
		flagChanges: List[FlagChangeShape]
	)

	object DhGlobalsShape {
		implicit val flagFormat = Json.format[FlagChangeShape]
		implicit val annoucementFormat = Json.format[AnnouncementShape]
		implicit val format = Json.format[DhGlobalsShape]

		def apply(v: JsValue): DhGlobalsShape = v.as[DhGlobalsShape]
	}
}
