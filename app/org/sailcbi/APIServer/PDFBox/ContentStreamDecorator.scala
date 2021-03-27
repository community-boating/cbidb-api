package org.sailcbi.APIServer.PDFBox

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT, Alignment}

import java.awt.Color

class ContentStreamDecorator(
	contentStream: PDPageContentStream,
	defaultFont: PDFont,
	defaultFontSize: Float,
	defaultColor: Color
) {

	val SIDE_MARGIN: Float = 50
	val TOP_MARGIN: Float = 40

	def close(): Unit = contentStream.close()

	private def writeTextAtPosition(
		text: String,
		x: Float,
		y: Float,
		font: PDFont = defaultFont,
		fontSize: Float = defaultFontSize,
		color: Color = defaultColor
	): Unit = {
		contentStream.setNonStrokingColor(color)
		contentStream.beginText()
		contentStream.setFont(font, fontSize)
		contentStream.newLineAtOffset(x, y - ContentStreamDecorator.getFontHeight(font, fontSize))
		contentStream.showText(text)
		contentStream.endText()
		contentStream.stroke()
	}


	def drawBox(contentStream: PDPageContentStream, left: Float, top: Float, width: Float, height: Float, thickness: Int, borderColor: Color, fillColor: Option[Color] = None): Unit = {
		if (thickness < 1) throw new Exception("Thickness must be >= 1")

		contentStream.setStrokingColor(borderColor)

		fillColor match {
			case Some(c) => {
				val t = thickness - 1
				contentStream.addRect(left + t, (top - height) - t, width - (2 * t), height - (2 * t))
				contentStream.setNonStrokingColor(c)
				contentStream.fill()
			}
			case _ =>
		}

		(0 until thickness).foreach(i => contentStream.addRect(left + i, (top - height) + i, width - (2 * i), height - (2 * i)))
		contentStream.stroke()
	}

	def writeBoxedTextAtPosition(
		font: PDFont,
		fontSize: Float,
		x: Float,
		y: Float,
		text: List[String],
		width: Float,
		align: Alignment,
		height: Float,
		thickness: Int,
		padding: Int,
		textColor: Color,
		borderColor: Color,
		fillColor: Option[Color] = None
	): Unit = {
		contentStream.setFont(font, fontSize)
		if (thickness >= 1) {
			drawBox(
				contentStream,
				x,
				y,
				width, //  + (2 * (thickness + padding)),
				height, //(text.length * getFontHeight(font, fontSize)) + ((text.length-1) * getFontSpacing(fontSize)) + (2 * (thickness + padding)),
				thickness,
				borderColor,
				fillColor
			)
		}

		text.indices.zip(text).foreach(f = tup => {
			val i = tup._1
			val t = tup._2
			val yPos = (y - thickness) - padding - (i * (ContentStreamDecorator.getFontHeight(font, fontSize) + ContentStreamDecorator.getFontSpacing(fontSize)))
			val leftBound = x + thickness + padding
			val rightBound = (x + width) - (thickness + padding)
			val stringWidth = ContentStreamDecorator.getFontStringWidth(font, fontSize, t)
			val left = align match {
				case ALIGN_LEFT => leftBound
				case ALIGN_RIGHT => rightBound - stringWidth
				case ALIGN_CENTER => leftBound + (((rightBound - leftBound) - stringWidth) / 2)
			}
			writeTextAtPosition(t, left, yPos, font, fontSize, textColor)
		})
	}
}

object ContentStreamDecorator {
	def getFontHeight(font: PDFont, fontSize: Float): Float = font.getFontDescriptor.getCapHeight / 1000 * fontSize

	def getFontSpacing(fontSize: Float): Float = fontSize / 3

	def getFontStringWidth(font: PDFont, fontSize: Float, text: String): Float = fontSize * font.getStringWidth(text) / 1000

	private def replaceBadChars(text: String, font: PDFont): String = {
		text.toCharArray.map(c => {
			val s = c.toString
			try {
				font.getStringWidth(s)
				s
			} catch {
				case _: Throwable => " "
			}
		}).mkString("")
	}

	// Given a chunk of text, horizontal max width, and font/fontSize, parititon the text into the largest substrings that will fit
	def wrapText(text: String, font: PDFont, fontSize: Float, width: Float): List[String] = {
		def recurse(text: String, font: PDFont, fontSize: Float, width: Float): List[String] = {
			def findWinningSplit(text: String, splitOn: Char): (String, String) = {
				if (text == "") ("", "")
				else {
					// e.g. List("This", "is", "a", "test")
					val words = text.split(splitOn)
					// List(List(), List("This"), List("This", "is"), List("This", "is", "a"), List("This", "is", "a", "test"))
					val firstPart = words.scanLeft(List(): List[String])((wordList, word) => wordList :+ word).map(l => l.mkString(splitOn.toString))
					// List(List("This", "is", "a", "test"), List("is", "a", "test"), List("a", "test"), List("test"), List())
					val secondPart = words.scanRight(List(): List[String])((word, wordList) => word :: wordList).map(l => l.mkString(splitOn.toString))
					// each element is a tuple of a substring plus the rest of the string
					val splits = firstPart zip secondPart
					// Find the largest substring that fits
					splits.takeWhile(ss => fontSize * font.getStringWidth(ss._1) / 1000 <= width).last
				}
			}

			// first split on newlines
			val lines: List[String] = text.split("\r\n").flatMap(l => l.split("\r")).flatMap(l => l.split("\n")).toList
			lines.flatMap(l => {
				val winningSplit = findWinningSplit(l, ' ')
				if (l == "") List.empty
				else if (winningSplit._1.isEmpty) {
					// line is still too long after splitting on spaces.
					// Find the longest substring that fits and recurse on the rest of the line
					val charByChar: List[String] = l.toCharArray.map(_.toString).toList
					val firstPart = charByChar.scanLeft(List(): List[String])((wordList, word) => wordList :+ word).map(l => l.mkString(""))
					val secondPart = charByChar.scanRight(List(): List[String])((word, wordList) => word :: wordList).map(l => l.mkString(""))
					val splits = firstPart zip secondPart
					val winningSplit = splits.takeWhile(ss => fontSize * font.getStringWidth(ss._1) / 1000 <= width).last
					winningSplit._1 :: recurse(winningSplit._2, font, fontSize, width)
				}
				else winningSplit._1 :: recurse(winningSplit._2, font, fontSize, width) // recurse over the rest of the text
			})
		}

		if (text == "") List("")
		else recurse(replaceBadChars(text, font), font, fontSize, width)
	}

	def truncateLine(text: String, font: PDFont, width: Int): String = ???
}
