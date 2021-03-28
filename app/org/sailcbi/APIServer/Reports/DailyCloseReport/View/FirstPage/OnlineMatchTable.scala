package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FirstPage

import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import com.coleji.framework.Util.Currency
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.DailyCloseReport
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.DailyCloseReportModel

class OnlineMatchTable(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val widthLeft = 150f
	val widthRight = 120f
	val header = DrawableTable(
		List(List("ONLINE")),
		List(widthLeft + widthRight),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize - 2
	)

	val cash: Currency = data.closeProps.onlineAR
	val tally: Currency = data.closeProps.onlineTally

	val body = DrawableTable(
		List(List("(Sage+Stripe) - Online Tally:", DailyCloseReport.getMatchString(cash, tally))),
		List(widthLeft, widthRight),
		List(ALIGN_RIGHT, ALIGN_RIGHT),
		defaultFont, defaultFontSize - 2
	)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		header.draw(contentStreamDecorator, left, top)
		body.draw(contentStreamDecorator, left, top - header.height)
	}
}
