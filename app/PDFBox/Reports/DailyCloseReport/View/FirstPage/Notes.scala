package PDFBox.Reports.DailyCloseReport.View.FirstPage

import java.awt.Color

import PDFBox.ContentStreamDecorator
import PDFBox.Drawable.{ALIGN_LEFT, Drawable}
import PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import org.apache.pdfbox.pdmodel.font.PDFont

class Notes(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable{
  val notes = data.closeProps.notes.getOrElse("")
  val notesBoxWidth = 500
  val notesMaxLines = 21
  val wrappedNotes = ContentStreamDecorator.wrapText(notes, defaultFont, defaultFontSize, notesBoxWidth)
  val trimmedNotes = {
    if (wrappedNotes.length <= notesMaxLines) wrappedNotes
    else wrappedNotes.take(notesMaxLines - 1) :+ "(...)"
  }
  def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
    contentStreamDecorator.writeBoxedTextAtPosition(
      defaultFont,
      defaultFontSize,
      left,
      top,
      List("Notes:"),
      maxWidth,
      ALIGN_LEFT,
      ContentStreamDecorator.getFontHeight(defaultFont,defaultFontSize),
      0,
      1,
      Color.BLACK,
      Color.BLACK
    )

    contentStreamDecorator.writeBoxedTextAtPosition(
      defaultFont,
      defaultFontSize,
      left,
      top-20,
      trimmedNotes,
      notesBoxWidth,
      ALIGN_LEFT,
      300,
      1,
      1,
      Color.BLACK,
      Color.BLACK
    )
  }
}
