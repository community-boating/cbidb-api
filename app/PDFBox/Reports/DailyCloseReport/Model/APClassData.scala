package PDFBox.Reports.DailyCloseReport.Model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class APClassData (
  val lastName: String,
  val firstName: String,
  val className: String,
  val firstSession: ZonedDateTime,
  val price: Currency,
  val payment: String
) extends RowData {
  val cellValues = List(
    lastName,
    firstName,
    className,
    firstSession.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
    price.toString,
    payment
  )
}
