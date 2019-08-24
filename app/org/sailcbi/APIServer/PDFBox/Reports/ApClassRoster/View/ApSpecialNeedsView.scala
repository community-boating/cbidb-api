package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.View

import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, DrawableTable, MultiDrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.ApSpecialNeedsData
import org.apache.pdfbox.pdmodel.font.PDFont

class ApSpecialNeedsView(
	data: List[ApSpecialNeedsData],
	headerFont: PDFont,
	bodyFont: PDFont,
	fontSize: Float
) extends AbstractTable[ApSpecialNeedsData](
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