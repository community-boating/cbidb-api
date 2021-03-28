package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.WaiversPrivsCards

import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.WaiverPrivData

class WaiversPrivsTable(
							   data: List[WaiverPrivData],
							   headerFont: PDFont,
							   bodyFont: PDFont,
							   fontSize: Float
					   ) extends AbstractTable[WaiverPrivData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("DAMAGE WAIVERS / GUEST PRIVS")),
		List(250f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("W/D", "IP / OL", "Full Name", "$")),
		List(50f, 55f, 100f, 45f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)