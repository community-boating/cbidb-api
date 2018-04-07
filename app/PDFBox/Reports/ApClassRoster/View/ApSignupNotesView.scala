package PDFBox.Reports.ApClassRoster.View

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable.{ALIGN_CENTER, ALIGN_LEFT, DrawableTable, MultiDrawableTable}
import PDFBox.Reports.ApClassRoster.Model.ApSignupNotesData
import org.apache.pdfbox.pdmodel.font.PDFont

class ApSignupNotesView(
  data: List[ApSignupNotesData],
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float
) extends AbstractTable[ApSignupNotesData](
  data,
  new MultiDrawableTable(List(DrawableTable(
    List(List("SIGNUP NOTES")),
    List(500f),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(List("Name", "Signup Note")),
    List(150f, 350f),
    List(ALIGN_CENTER, ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  List(ALIGN_LEFT, ALIGN_LEFT),
  bodyFont,
  fontSize,
  1,
  6
)