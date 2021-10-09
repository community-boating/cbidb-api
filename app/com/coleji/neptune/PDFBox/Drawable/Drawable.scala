package com.coleji.neptune.PDFBox.Drawable

import com.coleji.neptune.PDFBox.ContentStreamDecorator

abstract class Drawable {
	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit
}
