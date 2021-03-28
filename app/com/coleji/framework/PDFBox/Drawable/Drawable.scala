package com.coleji.framework.PDFBox.Drawable

import com.coleji.framework.PDFBox.ContentStreamDecorator

abstract class Drawable {
	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit
}
