package PDFBox.Reports.DailyCloseReport.View.ChecksARPage

import java.awt.Color

import PDFBox.ContentStreamDecorator
import PDFBox.Drawable.Drawable
import PDFBox.Reports.DailyCloseReport.Model.{AR, DailyCloseReportModel}
import org.apache.pdfbox.pdmodel.font.PDFont

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
			d.draw(contentStreamDecorator, left + 200, top)
		})
	}
}
