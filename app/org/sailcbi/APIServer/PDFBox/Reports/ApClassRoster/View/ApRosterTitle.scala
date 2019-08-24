package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.View

import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, Drawable, DrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.ApClassRosterModel
import org.apache.pdfbox.pdmodel.font.PDFont

class ApRosterTitle(data: ApClassRosterModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	//val date: String = data.instanceData.firstSessionTime.format(DateTimeFormatter.ofPattern("dd MMMM YYYY hh:mma (EE)"))
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
