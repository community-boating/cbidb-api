package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.GCSales

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.GCSalesData

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