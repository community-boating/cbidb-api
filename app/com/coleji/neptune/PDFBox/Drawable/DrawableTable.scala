package com.coleji.neptune.PDFBox.Drawable

import com.coleji.neptune.PDFBox.ContentStreamDecorator
import com.coleji.neptune.Util.FlatZip
import org.apache.pdfbox.pdmodel.font.PDFont

class DrawableTable(val rows: List[DrawableRow], thickness: Int) extends Drawable {
	def height: Float = rows.foldLeft(0f)((h, r) => h + r.height)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		val heights = rows.map(_.height)
		var y = top
		rows.zip(heights).foreach(tup => {
			val row = tup._1
			val height = tup._2
			row.draw(contentStreamDecorator, left, y)
			y -= (height - (thickness - 1))
		})
	}
}

object DrawableTable {
	def apply(
					 cells: List[List[String]],
					 widths: List[Float],
					 aligns: List[Alignment],
					 font: PDFont,
					 fontSize: Float,
					 thickness: Int = 1,
					 padding: Int = 4
			 ): DrawableTable = {
		val numberColumns = widths.length
		cells.foreach(c => {
			if (c.length != numberColumns) throw new Exception("Mismatched # of columns")
		})
		new DrawableTable(cells.map(row => {
			val zipped = FlatZip(row zip widths zip aligns)
			DrawableRow(zipped, font, fontSize, thickness, padding)
		}), thickness)
	}
}