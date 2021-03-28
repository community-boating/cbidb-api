package com.coleji.framework.PDFBox.Drawable

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.CbiUtil.Initializable
import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.ContentStreamDecorator._

import java.awt.Color

class DrawableCell(
						  val text: String,
						  val width: Float,
						  val align: Alignment,
						  val font: PDFont,
						  val fontSize: Float,
						  val thickness: Int,
						  val padding: Int,
						  val fixWidth: Boolean = true
				  ) {
	val stringWidth: Float = width - (2 * (thickness + padding))

	val lines: List[String] = wrapText(text, font, fontSize, stringWidth)

	val naturalHeight: Float =
		(2 * thickness) +
				(2 * padding) +
				(lines.length * getFontHeight(font, fontSize)) +
				((lines.length - 1) * getFontSpacing(fontSize))

	val naturalWidth: Float = lines.map(l => getFontStringWidth(font, fontSize, l)).reduce((a, b) => if (b > a) b else a)

	private val fixedHeight = new Initializable[Float]

	def setHeight(h: Float): Float = fixedHeight.set(h)

	def getHeight: Float = fixedHeight.getOrElse(naturalHeight)

	def draw(
					contentStreamDecorator: ContentStreamDecorator,
					left: Float,
					top: Float
			): Unit = contentStreamDecorator.writeBoxedTextAtPosition(
		font,
		fontSize,
		left,
		top,
		lines,
		width,
		align,
		getHeight,
		thickness,
		padding,
		Color.BLACK,
		Color.BLACK,
		None
	)
}
