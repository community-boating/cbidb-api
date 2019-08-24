package PDFBox.Reports.DailyCloseReport.View.FirstPage

import java.awt.Color

import PDFBox.ContentStreamDecorator
import PDFBox.Drawable.{ALIGN_LEFT, Drawable}
import PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont

class Notes(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float, document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator) extends Drawable {
	val notes = data.closeProps.notes.getOrElse("")
	val notesBoxWidth = 500
	val notesMaxLinesFirstPage = 20
	val notesMaxLinesWholePage = 50
	val wrappedNotes = ContentStreamDecorator.wrapText(notes, defaultFont, defaultFontSize, notesBoxWidth)
	val pagedNotes = {
		def page(remaining: List[String], paged: List[List[String]], pageSize: Int): List[List[String]] = {
			if (remaining.isEmpty) paged
			else {
				val afterSplit = remaining.splitAt(pageSize)
				page(afterSplit._2, afterSplit._1 :: paged, notesMaxLinesWholePage)
			}
		}
		page(wrappedNotes, List.empty, notesMaxLinesFirstPage).reverse
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
			ContentStreamDecorator.getFontHeight(defaultFont, defaultFontSize),
			0,
			1,
			Color.BLACK,
			Color.BLACK
		)

		contentStreamDecorator.writeBoxedTextAtPosition(
			defaultFont,
			defaultFontSize,
			left,
			top - 20,
			pagedNotes.headOption.getOrElse(List.empty),
			notesBoxWidth,
			ALIGN_LEFT,
			280,
			1,
			1,
			Color.BLACK,
			Color.BLACK
		)

		contentStreamDecorator.close()

		val pages = pagedNotes.tail
		pages.foreach(page => {
			val csd = newPage(document, PDRectangle.LETTER)

			csd.writeBoxedTextAtPosition(
				defaultFont,
				defaultFontSize,
				left,
				750,
				page,
				notesBoxWidth,
				ALIGN_LEFT,
				700,
				1,
				1,
				Color.BLACK,
				Color.BLACK
			)
			csd.close()
		})
	}
}
