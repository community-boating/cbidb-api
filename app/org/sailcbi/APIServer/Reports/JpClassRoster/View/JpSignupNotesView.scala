package org.sailcbi.APIServer.Reports.JpClassRoster.View

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, DrawableTable, MultiDrawableTable}
import org.sailcbi.APIServer.Reports.JpClassRoster.Model.JpSignupNotesData

class JpSignupNotesView(
							   data: List[JpSignupNotesData],
							   headerFont: PDFont,
							   bodyFont: PDFont,
							   fontSize: Float
					   ) extends AbstractTable[JpSignupNotesData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("DIRECTOR SIGNUP NOTES")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Name", "Director Signup Note")),
		List(150f, 350f),
		List(ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT),
	bodyFont,
	fontSize,
	1,
	6
)