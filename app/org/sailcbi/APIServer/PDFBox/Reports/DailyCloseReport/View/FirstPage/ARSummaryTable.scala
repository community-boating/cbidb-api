package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FirstPage

import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import org.apache.pdfbox.pdmodel.font.PDFont

class ARSummaryTable(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val widthLeft = 120f
	val widthRight = 120f
	val header = DrawableTable(
		List(List("A/R Summary")),
		List(widthLeft + widthRight),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize - 2
	)

	val body = DrawableTable(
		List(
			List("New A/R (excl. CC):", data.inPersonOtherARTotal.format()),
			List("Payment on A/R:", data.closeProps.arPaymentsTotal.format()),
			List("A/R Delta:", (data.inPersonOtherARTotal - data.closeProps.arPaymentsTotal).format())
		),
		List(widthLeft, widthRight),
		List(ALIGN_RIGHT, ALIGN_RIGHT),
		defaultFont, defaultFontSize - 2
	)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		header.draw(contentStreamDecorator, left, top)
		body.draw(contentStreamDecorator, left, top - header.height)
	}
}
