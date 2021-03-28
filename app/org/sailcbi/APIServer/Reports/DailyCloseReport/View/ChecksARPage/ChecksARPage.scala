package org.sailcbi.APIServer.Reports.DailyCloseReport.View.ChecksARPage

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable.Drawable
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.{AR, DailyCloseReportModel}

import java.awt.Color

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
