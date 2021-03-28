package com.coleji.framework.PDFBox.Drawable

import com.coleji.framework.PDFBox.ContentStreamDecorator
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle

abstract class MultiPageDrawable {
	def draw(document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator): Unit
}
