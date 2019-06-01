package PDFBox.Reports.JpClassRoster.View

import java.time.format.DateTimeFormatter

import PDFBox.ContentStreamDecorator
import PDFBox.Drawable.{ALIGN_CENTER, Drawable, DrawableTable}
import PDFBox.Reports.JpClassRoster.Model.JpClassRosterModel
import org.apache.pdfbox.pdmodel.font.PDFont

class JpRosterTitle(data: JpClassRosterModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val title: String = List(
		data.instanceData.firstSessionTime.format(DateTimeFormatter.ofPattern("hh:mma")),
		data.instanceData.typeName,
		"(" + data.instanceData.firstSessionTime.format(DateTimeFormatter.ofPattern("EE MM/dd/YYYY")) + ")",
	).mkString(" ")

	val dateTable = DrawableTable(
		List(List(title)),
		List(maxWidth - (2 * left)),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize, 0, 0
	)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		dateTable.draw(contentStreamDecorator, left, top)
	}
}
