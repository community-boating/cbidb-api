package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.APVouchers

import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.APVoucherData
import org.apache.pdfbox.pdmodel.font.PDFont

class APVouchersTable(
							 vouchers: List[APVoucherData],
							 headerFont: PDFont,
							 bodyFont: PDFont,
							 fontSize: Float
					 ) extends AbstractTable[APVoucherData](
	vouchers,
	new MultiDrawableTable(List(DrawableTable(
		List(List("AP VOUCHERS")),
		List(400f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Last Name", "First Name", "Price", "Void?")),
		List(125f, 125f, 75f, 75f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_LEFT),
	bodyFont,
	fontSize,
	1,
	3
)