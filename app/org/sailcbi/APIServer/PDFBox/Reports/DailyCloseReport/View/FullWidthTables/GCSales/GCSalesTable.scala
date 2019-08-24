package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.GCSales

import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.GCSalesData
import org.apache.pdfbox.pdmodel.font.PDFont

class GCSalesTable(
						  data: List[GCSalesData],
						  headerFont: PDFont,
						  bodyFont: PDFont,
						  fontSize: Float
				  ) extends AbstractTable[GCSalesData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("GIFT CERTIFICATES - SALES")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Purchaser", "Recipient", "Cert #", "Paid", "Value")),
		List(140f, 140f, 55f, 75f, 90f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_LEFT),
	bodyFont,
	fontSize,
	1,
	3
)