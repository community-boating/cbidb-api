package com.coleji.neptune.PDFBox

import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}

import java.awt.Color

abstract class PDFReport[T <: ReportModel](data: T) {
	val defaultFont: PDFont
	val defaultBoldFont: PDFont
	val defaultFontSize: Float
	val defaultColor: Color

	def appendToDocument(document: PDDocument): Unit

	val newPage: (PDDocument, PDRectangle) => ContentStreamDecorator = (document: PDDocument, pageShape: PDRectangle) => {
		val page = new PDPage(pageShape)
		document.addPage(page)
		new ContentStreamDecorator(new PDPageContentStream(document, page), defaultFont, defaultFontSize, defaultColor)
	}
}

object PDFReport {
	val MAX_WIDTH: Float = 612.0F
	val MAX_HEIGHT: Float = 792.0F
}