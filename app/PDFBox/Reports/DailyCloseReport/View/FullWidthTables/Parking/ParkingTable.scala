package PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Parking

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, DrawableTable, MultiDrawableTable}
import PDFBox.Reports.DailyCloseReport.Model.ParkingData
import org.apache.pdfbox.pdmodel.font.PDFont

class ParkingTable (
  data: ParkingData,
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float
) extends AbstractTable[ParkingData](
  List(data),
  new MultiDrawableTable(List(DrawableTable(
    List(List("PARKING TICKETS")),
    List(325f),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(List("# Open", "# Close", "+ / -", "# Comp", "# Sold", "$ Sold")),
    List(50f, 50f, 50f, 50f, 50f, 75f),
    List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  List(ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
  bodyFont,
  fontSize,
  1,
  3
)