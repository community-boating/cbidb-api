package com.coleji.framework.PDFBox.Abstract

import com.coleji.framework.PDFBox.Drawable.{Alignment, DrawableRow}
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.CbiUtil.FlatZip

abstract class RowData {
	def cellValues: List[String]

	def asRow(widths: List[Float], aligns: List[Alignment], pdfFont: PDFont, fontSize: Float, thickness: Int, padding: Int): DrawableRow =
		DrawableRow(FlatZip(cellValues zip widths zip aligns), pdfFont, fontSize, thickness, padding)
}
