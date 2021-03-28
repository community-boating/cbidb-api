package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FirstPage

import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.DailyCloseReportModel

class InPersonDetailTable(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val widthLeft = 120f
	val widthRight = 120f
	val header = DrawableTable(
		List(List("IN PERSON")),
		List(widthLeft + widthRight),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize - 2
	)

	val body = DrawableTable(
		List(
			List("Total Cash:", data.closeProps.cashTotal.format()),
			List("Total Checks:", data.closeProps.checkTotal.format()),
			List("Total Deposit:", (data.closeProps.cashTotal + data.closeProps.checkTotal).format()),
			List("Total CC:", data.inPersonCCTotal.format()),
			List("Total GC Redeemed:", data.inPersonGCRedeemed.format()),
			List("AP Class Vouchers:", data.inPersonAPVouchersRedeemed.format()),
			List("Total Other A/R:", data.inPersonOtherARTotal.format()),
			List("Total Revenue:", List(
				data.closeProps.cashTotal,
				data.closeProps.checkTotal,
				data.inPersonCCTotal,
				data.inPersonGCRedeemed,
				data.inPersonAPVouchersRedeemed,
				data.inPersonOtherARTotal
			).sum.format()),
			List("Total Sales (DB):", data.closeProps.inPersonTally.format()),
			List("Total POS (Tape):", data.closeProps.tapeValue.format()),
			List("Total Tax Amount:", data.taxRevenue.format())
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
