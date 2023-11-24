package org.sailcbi.APIServer.Api.Endpoints.PDFReports

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.{PDType1Font, Standard14Fonts}
import org.sailcbi.APIServer.Reports.DailyCloseReport.DailyCloseReport
import org.sailcbi.APIServer.Reports.DailyCloseReport.Loader.{DailyCloseReportLiveLoader, DailyCloseReportLiveParameter}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.http.HttpEntity
import play.api.mvc._

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RunDailyCloseReport @Inject() (implicit val exec: ExecutionContext) extends InjectedController {
	def get(closeId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		val pr = ParsedRequest(req)

		PA.withRequestCache(StaffRequestCache)(None, pr, rc => {

			val output = new ByteArrayOutputStream()
			val document: PDDocument = new PDDocument()

			val pdfFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA)
			val fontSize = 13

			val model = DailyCloseReportLiveLoader(DailyCloseReportLiveParameter(closeId), rc)

			val report = new DailyCloseReport(model)

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

