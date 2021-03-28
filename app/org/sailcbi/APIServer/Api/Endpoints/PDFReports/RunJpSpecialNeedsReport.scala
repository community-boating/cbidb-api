package org.sailcbi.APIServer.Api.Endpoints.PDFReports

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.apache.pdfbox.pdmodel.PDDocument
import org.sailcbi.APIServer.CbiUtil.{DateUtil, ParsedRequest}
import org.sailcbi.APIServer.Reports.JpSpecialNeedsReport.JpSpecialNeedsReport
import org.sailcbi.APIServer.Reports.JpSpecialNeedsReport.Loader.{JpSpecialNeedsReportLiveLoader, JpSpecialNeedsReportLiveParameter}
import org.sailcbi.APIServer.UserTypes.ApexRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.http.HttpEntity
import play.api.mvc._

import java.io.ByteArrayOutputStream
import java.time.ZonedDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RunJpSpecialNeedsReport @Inject() (implicit exec: ExecutionContext) extends InjectedController {
	def get(from: String, to: String, signet: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val fromZDT: ZonedDateTime = DateUtil.toBostonTime(DateUtil.parse(from))
		val toZDT: ZonedDateTime = DateUtil.toBostonTime(DateUtil.parse(to))
		val logger = PA.logger
		val pr = ParsedRequest(req)
				.addHeader("apex-signet", signet.getOrElse(""))
		/*.addHeader("pas-userName", userName)
		.addHeader("pas", pas)
		.addHeader("pas-procName", "DAILY_CLOSE_REPORT")
		.addHeader("pas-argString", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)*/

		PA.withRequestCache(ApexRequestCache)(None, pr, rc => {
			/* val verifyPas: Boolean =
			   rc.executePreparedQueryForSelect(new VerifyPas(userName, pas, "DAILY_CLOSE_REPORT", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)).head

			 if (!verifyPas) throw new BadPasException
		 */
			val output = new ByteArrayOutputStream()
			val document: PDDocument = new PDDocument()

			val model = JpSpecialNeedsReportLiveLoader(JpSpecialNeedsReportLiveParameter(fromZDT, toZDT), rc)
			val report = new JpSpecialNeedsReport(model)
			report.appendToDocument(document)


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
