package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RatingHtml @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val programsQuery = new PreparedQueryForSelect[Int](Set(StaffRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override def getQuery: String =
					s"""
					   |select distinct program_id from persons_ratings where person_id = $personId
					   |""".stripMargin
			}

			val programIdList = rc.executePreparedQueryForSelect(programsQuery)
			val programs = programIdList.toSet

			val q = new PreparedQueryForSelect[List[RatingHtmlReturn]](Set(StaffRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): List[RatingHtmlReturn] = List(
					RatingHtmlReturn(MagicIds.PROGRAM_TYPES.ADULT_PROGRAM_ID, rsw.getString(1)),
					RatingHtmlReturn(MagicIds.PROGRAM_TYPES.JUNIOR_PROGRAM_ID, rsw.getString(2)),
					RatingHtmlReturn(MagicIds.PROGRAM_TYPES.HS_PROGRAM_ID, rsw.getString(3)),
				)

				override def getQuery: String =
					s"""
					  |select
					  |ratings_pkg.ap_ratings($personId),
					  |ratings_pkg.jp_ratings($personId),
					  |ratings_pkg.hs_ratings($personId)
					  |from dual
					  |""".stripMargin
			}

			val ratings = rc.executePreparedQueryForSelect(q).head.toIndexedSeq
			implicit val format: OFormat[RatingHtmlReturn] = RatingHtmlReturn.format

			Future(Ok(Json.toJson(ratings.filter(r => programs.contains(r.programId)))))
		})
	})

	case class RatingHtmlReturn(programId: Int, ratingsHtml: String)

	object RatingHtmlReturn {
		implicit val format = Json.format[RatingHtmlReturn]

		def apply(v: JsValue): RatingHtmlReturn = v.as[RatingHtmlReturn]
	}
}