package PDFBox.Reports.DailyCloseReport.View.FirstPage

import java.awt.Color

import PDFBox.Drawable.Drawable
import PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import PDFBox.{ContentStreamDecorator, PDFReport}
import org.apache.pdfbox.pdmodel.font.PDFont

class FirstPage(data: DailyCloseReportModel, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, defaultColor: Color, left: Float, top: Float) extends Drawable{
  def draw(contentStreamDecorator: ContentStreamDecorator): Unit =
    draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float)

  def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
    new Title(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left, top)
    new InPersonMatchTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left, top-40)
    new InPersonDetailTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left, top-100)
    new OnlineMatchTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left+230, top-40)
    new OnlineDetailTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left+270, top-100)
    new StaffTables(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left, top-330)
    new Notes(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
      .draw(contentStreamDecorator, left, top-430)
  }
}
