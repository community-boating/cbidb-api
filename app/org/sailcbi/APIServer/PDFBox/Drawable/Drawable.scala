package org.sailcbi.APIServer.PDFBox.Drawable

import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator

abstract class Drawable {
	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit
}
