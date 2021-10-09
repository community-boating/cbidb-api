package com.coleji.neptune.PDFBox.Abstract

import com.coleji.neptune.PDFBox.Drawable.{Alignment, DrawableRow}
import com.coleji.neptune.Util.FlatZip
import org.apache.pdfbox.pdmodel.font.PDFont

abstract class RowData {
	def cellValues: List[String]

	def asRow(widths: List[Float], aligns: List[Alignment], pdfFont: PDFont, fontSize: Float, thickness: Int, padding: Int): DrawableRow =
		DrawableRow(FlatZip(cellValues zip widths zip aligns), pdfFont, fontSize, thickness, padding)
}
