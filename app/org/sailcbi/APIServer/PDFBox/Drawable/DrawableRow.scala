package org.sailcbi.APIServer.PDFBox.Drawable

import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.apache.pdfbox.pdmodel.font.PDFont

class DrawableRow(val cells: List[DrawableCell], thickness: Int) {
	def height: Float = cells.map(_.getHeight).reduce((a, b) => if (b > a) b else a)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		val widths = cells.map(_.width)
		var x = left
		cells.zip(widths).foreach(tup => {
			val cell = tup._1
			val width = tup._2
			cell.draw(contentStreamDecorator, x, top)
			x += (width - (thickness - 1))
		})
	}
}

object DrawableRow {
	def apply(cellData: List[(String, Float, Alignment)], font: PDFont, fontSize: Float, thickness: Int, padding: Int): DrawableRow = {
		val cells = cellData.map(tup => {
			val cell: String = tup._1
			val width: Float = tup._2
			val align: Alignment = tup._3
			new DrawableCell(
				cell,
				width,
				align,
				font,
				fontSize,
				thickness,
				padding,
				true
			)
		})
		val maxHeight = cells.map(_.naturalHeight).reduce((a, b) => if (b > a) b else a)
		cells.foreach(_.setHeight(maxHeight))
		new DrawableRow(cells, thickness)
	}
}