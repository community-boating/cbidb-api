package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class APVoucherData (
  val lastName: String,
  val firstName: String,
  val price: Currency,
  val isVoid: Boolean
) extends RowData {
  val cellValues = List(
    lastName,
    firstName,
    price.toString,
    if(isVoid) "(VOID)" else ""
  )
}
