package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.Memberships

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable._
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.MembershipSale

class MembershipsTable(
							  membershipSales: List[MembershipSale],
							  headerFont: PDFont,
							  bodyFont: PDFont,
							  fontSize: Float
					  ) extends AbstractTable[MembershipSale](
	membershipSales,
	new MultiDrawableTable(List(DrawableTable(
		List(List("MEMBERSHIPS / HS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Full Name", "Membership", "Discount", "In-Person / Online", "Price")),
		List(125f, 150f, 110f, 60f, 55f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
) {
	val headers = List("Full Name", "Membership", "Discount", "In-Person / Online", "Price")
	val aligns = List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT)
}