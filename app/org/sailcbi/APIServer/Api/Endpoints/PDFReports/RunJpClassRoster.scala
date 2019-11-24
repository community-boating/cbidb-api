package org.sailcbi.APIServer.Api.Endpoints.PDFReports

import java.io.ByteArrayOutputStream

import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.inject.Inject
import org.apache.pdfbox.pdmodel.PDDocument
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.JpClassRoster
import org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Loader.{JpClassRosterLiveLoader, JpClassRosterLiveParameter}
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.http.HttpEntity
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class RunJpClassRoster @Inject() (implicit PA: PermissionsAuthority, exec: ExecutionContext) extends InjectedController {
	def get(instanceIds: String, signet: Option[String]): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		val pr = ParsedRequest(req)
				.addHeader("apex-signet", signet.getOrElse(""))
		/*.addHeader("pas-userName", userName)
		.addHeader("pas", pas)
		.addHeader("pas-procName", "DAILY_CLOSE_REPORT")
		.addHeader("pas-argString", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)*/
		PA.withRequestCache(ApexUserType, None, pr, rc => {
			val pb = rc.pb
			/* val verifyPas: Boolean =
			   pb.executePreparedQueryForSelect(new VerifyPas(userName, pas, "DAILY_CLOSE_REPORT", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)).head

			 if (!verifyPas) throw new BadPasException
		 */
			val output = new ByteArrayOutputStream()
			val document: PDDocument = new PDDocument()

			val instanceIdsInts = instanceIds.split(",").map(_.toInt)
			instanceIdsInts.foreach(id => {
				val model = JpClassRosterLiveLoader(JpClassRosterLiveParameter(id), pb)
				val report = new JpClassRoster(model)
				report.appendToDocument(document)
			})

			// Finally Let's save the PDF
			document.save(output)
			document.close()

			val source: Source[ByteString, _] = Source.single(ByteString(output.toByteArray))
			Future(Result(
				header = ResponseHeader(200, Map()),
				body = HttpEntity.Streamed(source, Some(output.toByteArray.length), Some("application/pdf"))
			))
		})
	}}
}
