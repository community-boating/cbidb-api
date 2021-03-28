package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.JPClasses

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable._
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.JPClassData

class JPClassTable(
						  signups: List[JPClassData],
						  headerFont: PDFont,
						  bodyFont: PDFont,
						  fontSize: Float
				  ) extends AbstractTable[JPClassData](
	signups,
	new MultiDrawableTable(List(DrawableTable(
		List(List("JP CLASS SIGNUPS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Last Name", "First Name", "Class Name", "First Session", "Discount", "Price")),
		List(124f, 124f, 74f, 74f, 55f, 49f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)