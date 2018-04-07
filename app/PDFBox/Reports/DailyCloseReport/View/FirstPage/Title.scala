package PDFBox.Reports.DailyCloseReport.View.FirstPage

import java.time.format.DateTimeFormatter

import PDFBox.ContentStreamDecorator
import PDFBox.Drawable.{ALIGN_CENTER, Drawable, DrawableTable}
import PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import org.apache.pdfbox.pdmodel.font.PDFont

class Title(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
  val titleTable = DrawableTable(
    List(List("CBI Daily Close Report")),
    List(maxWidth - (2 * left)),
    List(ALIGN_CENTER),
    defaultBoldFont, defaultFontSize, 0, 0
  )
  val date: String = data.closeProps.closedDatetime match {
    case Some(d) => d.format(DateTimeFormatter.ofPattern("dd MMMM YYYY hh:mma (EE)"))
    case None => "(close still open)"
  }
  val dateTable = DrawableTable(
    List(List(date)),
    List(maxWidth - (2 * left)),
    List(ALIGN_CENTER),
    defaultFont, defaultFontSize, 0, 0
  )
  def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
    titleTable.draw(contentStreamDecorator, left, top)
    dateTable.draw(contentStreamDecorator, left, top - 15)
  }
}
