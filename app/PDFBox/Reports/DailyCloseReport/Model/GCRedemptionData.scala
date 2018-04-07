package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class GCRedemptionData(
  val lastName: String,
  val firstName: String,
  val certNumber: String,
  val usedFor: String,
  val amount: Currency
) extends RowData {
  val cellValues = List(
    lastName,
    firstName,
    certNumber,
    usedFor,
    amount.format()
  )
}
