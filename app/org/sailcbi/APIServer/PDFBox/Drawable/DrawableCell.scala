package org.sailcbi.APIServer.PDFBox.Drawable

import java.awt.Color

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator._
import org.apache.pdfbox.pdmodel.font.PDFont

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
