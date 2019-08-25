package org.sailcbi.APIServer.PDFBox.Drawable

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator

abstract class MultiPageDrawable {
	def draw(document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator): Unit
}
