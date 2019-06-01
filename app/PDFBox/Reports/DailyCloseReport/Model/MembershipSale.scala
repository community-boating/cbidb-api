package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class MembershipSale(
							val closeId: Int,
							val fullName: String,
							val membershipType: String,
							val discountAmount: Option[Currency],
							val discountName: Option[String],
							val location: String,
							val price: Currency
					) extends RowData {
	val cellValues = List(
		fullName,
		membershipType,
		discountAmount match {
			case None => ""
			case Some(amt) => amt.format() + " - " + discountName.getOrElse("")
		},
		location,
		price.toString
	)
}
