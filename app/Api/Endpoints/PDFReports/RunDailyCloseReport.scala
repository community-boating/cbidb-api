package Api.Endpoints.PDFReports

import java.io.ByteArrayOutputStream

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import PDFBox.Reports.DailyCloseReport.DailyCloseReport
import PDFBox.Reports.DailyCloseReport.Loader.{DailyCloseReportLiveLoader, DailyCloseReportLiveParameter}
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.inject.Inject
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType1Font
import play.api.http.HttpEntity
import play.api.mvc._


class RunDailyCloseReport @Inject() extends AuthenticatedRequest {
  def get(closeId: Int, signet: Option[String]): Action[AnyContent] = Action {req => {
    val logger = PermissionsAuthority.logger
    val rc = getRC(
      ApexUserType,
      ParsedRequest(req)
        .addHeader("apex-signet", signet.getOrElse(""))
        /*.addHeader("pas-userName", userName)
        .addHeader("pas", pas)
        .addHeader("pas-procName", "DAILY_CLOSE_REPORT")
        .addHeader("pas-argString", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)*/
    )
    val pb = rc.pb
   /* val verifyPas: Boolean =
      pb.executePreparedQueryForSelect(new VerifyPas(userName, pas, "DAILY_CLOSE_REPORT", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)).head

    if (!verifyPas) throw new BadPasException
*/
    val output = new ByteArrayOutputStream()
    val document: PDDocument = new PDDocument()

    val pdfFont = PDType1Font.HELVETICA
    val fontSize = 13

    val model = DailyCloseReportLiveLoader(DailyCloseReportLiveParameter(closeId), pb)

    val report = new DailyCloseReport(model)

    report.appendToDocument(document)

    // Finally Let's save the PDF
    document.save(output)
    document.close()

    val source: Source[ByteString, _] = Source.single(ByteString(output.toByteArray))
    Result(
      header = ResponseHeader(200, Map()),
      body = HttpEntity.Streamed(source, Some(output.toByteArray.length), Some("application/pdf"))
    )
  }}
}

