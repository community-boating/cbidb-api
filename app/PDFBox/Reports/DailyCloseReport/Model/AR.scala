package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class AR (val sourceName: String, val value: Currency) extends RowData {
  def cellValues: List[String] =  List(sourceName, value.format())
}
