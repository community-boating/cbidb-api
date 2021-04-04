package org.sailcbi.APIServer

import org.krysalis.barcode4j.impl.code128.Code128Bean
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

object BarcodeFactory {
	def getImage(text: String): BufferedImage = {
		val barcodeGenerator = new Code128Bean()
		val canvas = new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0)
		barcodeGenerator.generateBarcode(canvas, text)
		canvas.getBufferedImage
	}

	def getBase64FromImage(image: BufferedImage, imageFormat: String): String = {
		val bos = new ByteArrayOutputStream()

		ImageIO.write(image, imageFormat, bos)
		val imageBytes = bos.toByteArray

		val encoder = Base64.getEncoder
		val bytes = encoder.encode(imageBytes)

		bos.close()

		bytes.map(_.toChar).mkString
	}
}
