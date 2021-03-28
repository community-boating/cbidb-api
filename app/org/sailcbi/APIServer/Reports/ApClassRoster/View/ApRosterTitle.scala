package org.sailcbi.APIServer.Reports.ApClassRoster.View

import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, Drawable, DrawableTable}
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.ApClassRoster.Model.ApClassRosterModel

import java.time.format.DateTimeFormatter

class ApRosterTitle(data: ApClassRosterModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val title: String = List(
		data.instanceData.firstSessionTime.format(DateTimeFormatter.ofPattern("hh:mma")),
		data.instanceData.typeName,
		"(" + data.instanceData.firstSessionTime.format(DateTimeFormatter.ofPattern("EE MM/dd/yyyy")) + ")",
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
