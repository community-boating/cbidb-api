package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.APVouchers

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.APVoucherData

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