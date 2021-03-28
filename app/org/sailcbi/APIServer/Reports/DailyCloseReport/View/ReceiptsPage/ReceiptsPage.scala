package org.sailcbi.APIServer.Reports.DailyCloseReport.View.ReceiptsPage

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.{DailyCloseReportModel, Denoms}

import java.awt.Color

class ReceiptsPage(data: DailyCloseReportModel, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, defaultColor: Color, left: Float, top: Float) extends Drawable {
	val leftWidth = 55f
	val rightWidth = 70f

	def draw(contentStreamDecorator: ContentStreamDecorator): Unit =
		draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		def makeReceiptsTable(title: String, denoms: Denoms): Drawable = {
			val padding = 5
			val fontSize = defaultFontSize - 3
			val header = DrawableTable(
				List(List(title)),
				List(leftWidth + rightWidth),
				List(ALIGN_CENTER),
				defaultFont,
				fontSize, 1, padding
			)
			val colHeaders = DrawableTable(
				List(List("Denom", "Value")),
				List(leftWidth, rightWidth),
				List(ALIGN_CENTER, ALIGN_CENTER),
				defaultFont,
				fontSize, 1, padding
			)
			val denomsTable = DrawableTable(
				denoms.asListOfList,
				List(leftWidth, rightWidth),
				List(ALIGN_RIGHT, ALIGN_RIGHT),
				defaultFont,
				fontSize, 1, padding
			)
			val totalTable = DrawableTable(
				List(List("TOTAL:", denoms.total.format())),
				List(leftWidth, rightWidth),
				List(ALIGN_RIGHT, ALIGN_RIGHT),
				defaultBoldFont,
				fontSize, 1, 3
			)

			object Result extends Drawable {
				def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
					header.draw(contentStreamDecorator, left, top)
					colHeaders.draw(contentStreamDecorator, left, top - header.height)
					denomsTable.draw(contentStreamDecorator, left, top - (header.height + colHeaders.height))
					totalTable.draw(contentStreamDecorator, left, top - (header.height + colHeaders.height + denomsTable.height))
				}
			}
			Result
		}

		val width = 135
		val col1 = 40
		val col2 = col1 + width
		val col3 = col2 + width
		val col4 = col3 + width

		val row1 = 740
		val row2 = 380

		val receipts = makeReceiptsTable("RECEIPTS", Denoms.getDenoms(data.cash, "RE"))
		val regOpen = makeReceiptsTable("REG OPEN", Denoms.getDenoms(data.cash, "RO"))
		val regClose = makeReceiptsTable("REG CLOSE", Denoms.getDenoms(data.cash, "RC"))
		val backOpen = makeReceiptsTable("BACK OPEN", Denoms.getDenoms(data.cash, "BO"))
		val backClose = makeReceiptsTable("BACK CLOSE", Denoms.getDenoms(data.cash, "BC"))
		val safeOpen = makeReceiptsTable("SAFE OPEN", Denoms.getDenoms(data.cash, "SO"))
		val safeClose = makeReceiptsTable("SAFE CLOSE", Denoms.getDenoms(data.cash, "SC"))
		receipts.draw(contentStreamDecorator, col2, row1)

		regOpen.draw(contentStreamDecorator, col3, row1)
		regClose.draw(contentStreamDecorator, col4, row1)

		backOpen.draw(contentStreamDecorator, col1, row2)
		backClose.draw(contentStreamDecorator, col2, row2)

		safeOpen.draw(contentStreamDecorator, col3, row2)
		safeClose.draw(contentStreamDecorator, col4, row2)
	}
}