package PDFBox.Reports.ApClassRoster.View

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.ApClassRoster.Model.{ApClassRosterModel, ApRosterData}
import org.apache.pdfbox.pdmodel.font.PDFont

class ApClassRosterView(
  data: ApClassRosterModel,
  roster: List[ApRosterData],
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float,
  title: String
) extends AbstractTable[ApRosterData](
  roster,
  new MultiDrawableTable(List(DrawableTable(
    List(List(title)),
    List(ApClassRosterView.getWidths(data).sum),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(ApClassRosterView.getHeaderTexts(data)),
    ApClassRosterView.getWidths(data),
    ApClassRosterView.getHeaderAligns(data),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  ApClassRosterView.getBodyAligns(data),
  bodyFont,
  fontSize,
  1,
  6
)

object ApClassRosterView {
  def getHeaderTexts(data: ApClassRosterModel): List[String] = "Name" :: ((1 to data.instanceData.numberSessions).map("Day " + _) :+ "Pass" ).toList
  def getWidths(data: ApClassRosterModel): List[Float] = 270f :: (1 to data.instanceData.numberSessions+1).map(_ => 40f).toList
  def getHeaderAligns(data: ApClassRosterModel): List[Alignment] = (1 to data.instanceData.numberSessions+2).map(_ => ALIGN_CENTER).toList
  def getBodyAligns(data: ApClassRosterModel): List[Alignment] = (1 to data.instanceData.numberSessions+2).map(_ => ALIGN_LEFT).toList
}