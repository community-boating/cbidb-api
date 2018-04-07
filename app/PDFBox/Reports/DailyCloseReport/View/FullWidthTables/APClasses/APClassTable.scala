package PDFBox.Reports.DailyCloseReport.View.FullWidthTables.APClasses

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.DailyCloseReport.Model.APClassData
import org.apache.pdfbox.pdmodel.font.PDFont

class APClassTable  (
  signups: List[APClassData],
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float
) extends AbstractTable[APClassData](
  signups,
  new MultiDrawableTable(List(DrawableTable(
    List(List("AP CLASS SIGNUPS")),
    List(502f),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(List("Last Name", "First Name", "Class Name", "First Session", "Price", "Payment")),
    List(124f, 124f, 76f, 74f, 54f, 50f),
    List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT),
  bodyFont,
  fontSize,
  1,
  3
)