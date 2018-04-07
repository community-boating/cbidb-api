package PDFBox.Reports.JpSpecialNeedsReport.View

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, DrawableTable, MultiDrawableTable}
import PDFBox.Reports.JpSpecialNeedsReport.Model.JpSpecialNeedsData
import org.apache.pdfbox.pdmodel.font.PDFont

class JpSpecialNeedsView(
  from: ZonedDateTime,
  to: ZonedDateTime,
  data: List[JpSpecialNeedsData],
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float
) extends AbstractTable[JpSpecialNeedsData](
  data,
  new MultiDrawableTable(List(DrawableTable(
    List(List("SPECIAL NEEDS: " + from.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " + to.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))),
    List(545f),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(List("Name", "Allergies", "Medications", "Special Needs")),
    List(140f, 135f, 135f, 135f),
    List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT),
  bodyFont,
  fontSize,
  1,
  6
)