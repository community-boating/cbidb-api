package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.GCComps

import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.GCCompsData

class GCCompsTable(
						  data: List[GCCompsData],
						  headerFont: PDFont,
						  bodyFont: PDFont,
						  fontSize: Float
				  ) extends AbstractTable[GCCompsData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("GIFT CERTIFICATES - COMPS")),
		List(400f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Recipient", "Cert #", "Value")),
		List(240f, 75f, 85f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)