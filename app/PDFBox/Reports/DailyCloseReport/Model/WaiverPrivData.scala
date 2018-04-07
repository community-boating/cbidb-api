package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class WaiverPrivData(
  val identifier: String,
  val location: String,
  val fullName: String,
  val price: Currency
) extends RowData {
  val cellValues = List(
    identifier,
    location,
    fullName,
    price.format()
  )
}