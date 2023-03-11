package org.sailcbi.APIServer.Api.Endpoints.Staff.slacksyncusers

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.apache.commons.csv.CSVFormat
import org.sailcbi.APIServer.Logic.{MembershipLogic, SlackSyncLogic}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import java.io.{FileInputStream, FileReader}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SlackSyncUsers @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			val mpf = parsedRequest.bodyMultipartFormData
			val file = mpf.get.files.head.ref

			import scala.collection.JavaConverters._
			val records = CSVFormat.RFC4180
				.withFirstRecordAsHeader()
				.parse(new FileReader(file.path.toFile)).getRecords.asScala.toList

			val userRecords = records.map(_.toMap.asScala.toMap).map(SlackUserDto.construct)

			val failures = userRecords.filter(r => r.isLeft)

			if (failures.nonEmpty) {
				Future(BadRequest(ValidationResult.from(failures(0).swap.getOrElse("")).toResultError.asJsObject))
			} else {
				val dtos = userRecords.map(r => r.getOrElse(null))
				implicit val format = SlackSyncResponse.format
				Future(Ok(Json.toJson(SlackSyncLogic.processSlackUserSync(rc, dtos))))
			}
		})
	}
}
