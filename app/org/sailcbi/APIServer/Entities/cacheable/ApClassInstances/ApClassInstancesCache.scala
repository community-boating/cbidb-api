package org.sailcbi.APIServer.Entities.cacheable.ApClassInstances

import com.coleji.neptune.Core.{Cacheable, RequestCache}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, PublicRequestCache, StaffRequestCache}

import java.time.Duration
import java.time.format.DateTimeFormatter

object ApClassInstancesCache extends Cacheable[ApClassInstancesCacheKey, List[ApClassInstanceDto]]{
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: ApClassInstancesCacheKey): String = CacheKeys.apClassInstances(config)

	override protected def serialize(result: List[ApClassInstanceDto]): String = Serde.serializeList(result)

	override protected def deseralize(resultString: String): List[ApClassInstanceDto] = Serde.deserializeList(resultString)

	override protected def generateResult(rc: RequestCache, config: ApClassInstancesCacheKey): List[ApClassInstanceDto] = {
		val startDateString: String = config.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

		val q = new PreparedQueryForSelect[ApClassInstanceDto](Set(PublicRequestCache, MemberRequestCache, StaffRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ApClassInstanceDto = ApClassInstanceDto(
				rsw.getInt(1),
				rsw.getString(2),
				rsw.getString(3),
				rsw.getString(4),
				rsw.getString(5),
				rsw.getInt(6)
			)

			override def getQuery: String =
				s"""
				   |select i.instance_id, t.type_name, to_char(fs.session_datetime, 'MM/DD/YYYY') as start_date, to_char(fs.session_datetime, 'HH:MIPM') as start_time, i.location_string,
				   |(select count(*) from ap_class_signups where instance_id = i.instance_id and signup_type = 'E') as enrollees
				   |from ap_class_types t, ap_class_formats f, ap_class_instances i, ap_class_bookends bk, ap_class_sessions fs
				   |where i.instance_id = bk.instance_id and bk.first_session = fs.session_id
				   |and i.format_id = f.format_id and f.type_id = t.type_id
				   |and trunc(fs.session_datetime) = to_date('$startDateString','MM/DD/YYYY')
				   |and i.cancelled_datetime is null
				   |and nvl(i.hide_online,'N') <> 'Y'
				   |order by fs.session_datetime
				   |
    """.stripMargin
		}

		rc.executePreparedQueryForSelect(q)
	}
}
