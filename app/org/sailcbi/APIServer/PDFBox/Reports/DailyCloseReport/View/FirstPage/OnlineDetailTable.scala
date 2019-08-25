package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FirstPage

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel

class OnlineDetailTable(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val widthLeft = 130f
	val widthRight = 120f
	val header = DrawableTable(
		List(List("ONLINE")),
		List(widthLeft + widthRight),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize - 2
	)

	val body = DrawableTable(
		List(
			List("Sage Revenue:", data.sageRevenue.format()),
			List("Stripe Revenue:", data.stripeRevenue.format()),
			List("Online GC Redeemed:", data.onlineGCRedeemed.format()),
			List("Online AP Vouchers:", data.onlineAPVouchersRedeemed.format()),
			List("Total Online Revenue:", data.closeProps.onlineAR.format()),
			List("Total Online Sales:", data.closeProps.onlineTally.format())
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
