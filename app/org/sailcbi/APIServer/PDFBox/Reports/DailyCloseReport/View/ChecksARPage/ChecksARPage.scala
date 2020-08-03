package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.ChecksARPage

import java.awt.Color

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.Drawable.Drawable
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.{AR, DailyCloseReportModel}

class ChecksARPage(data: DailyCloseReportModel, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, defaultColor: Color, left: Float, top: Float) extends Drawable {
	val arTable = new ARTable(
		new AR("Stripe", data.stripeRevenue) :: data.ar,
		defaultFont,
		defaultFontSize - 1
	)

	val checksTable = new ChecksTable(
		data.checks,
		defaultFont,
		defaultFontSize - 1
	)

	def draw(contentStreamDecorator: ContentStreamDecorator): Unit =
		draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		arTable.getDrawables(710, None, Some(1)).head.draw(contentStreamDecorator, left, top)
		val checksDrawables = checksTable.getDrawables(710, None, Some(1))
		checksDrawables.foreach(d => {
			d.draw(contentStreamDecorator, left + 170, top)
		})
	}
}
