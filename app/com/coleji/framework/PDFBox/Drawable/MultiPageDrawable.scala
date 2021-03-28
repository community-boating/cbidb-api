package com.coleji.framework.PDFBox.Drawable

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import com.coleji.framework.PDFBox.ContentStreamDecorator

abstract class MultiPageDrawable {
	def draw(document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator): Unit
}
