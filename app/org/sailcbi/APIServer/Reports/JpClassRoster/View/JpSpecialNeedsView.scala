package org.sailcbi.APIServer.Reports.JpClassRoster.View

import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, DrawableTable, MultiDrawableTable}
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.JpClassRoster.Model.JpSpecialNeedsData

class JpSpecialNeedsView(
	data: List[JpSpecialNeedsData],
	headerFont: PDFont,
	bodyFont: PDFont,
	fontSize: Float
) extends AbstractTable[JpSpecialNeedsData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("SPECIAL NEEDS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Name", "Allergies", "Medications", "Special Needs")),
		List(125f, 125f, 125f, 125f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT),
	bodyFont,
	fontSize,
	1,
	6
)