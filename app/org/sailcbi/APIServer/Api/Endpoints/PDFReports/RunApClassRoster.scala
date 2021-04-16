package org.sailcbi.APIServer.Api.Endpoints.PDFReports

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.sailcbi.APIServer.Reports.ApClassRoster.ApClassRoster
import org.sailcbi.APIServer.Reports.ApClassRoster.Loader.{ApClassRosterLiveLoader, ApClassRosterLiveParameter}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.http.HttpEntity
import play.api.mvc._

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RunApClassRoster @Inject() (implicit exec: ExecutionContext) extends InjectedController {
	def get(instanceId: Int, signet: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		val pr = ParsedRequest(req)
				.addHeader("apex-signet", signet.getOrElse(""))
//				.addHeader("pas-userName", userName)
//				.addHeader("pas", pas)
//				.addHeader("pas-procName", "DAILY_CLOSE_REPORT")
//				.addHeader("pas-argString", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)
		PA.withRequestCache(StaffRequestCache)(None, pr, rc => {
			/* val verifyPas: Boolean =
			   rc.executePreparedQueryForSelect(new VerifyPas(userName, pas, "DAILY_CLOSE_REPORT", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)).head

			 if (!verifyPas) throw new BadPasException
		 */
			val output = new ByteArrayOutputStream()
			val document: PDDocument = new PDDocument()

			val pdfFont = PDType1Font.HELVETICA
			val fontSize = 13

			val model = ApClassRosterLiveLoader(ApClassRosterLiveParameter(instanceId), rc)

			val report = new ApClassRoster(model)

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

