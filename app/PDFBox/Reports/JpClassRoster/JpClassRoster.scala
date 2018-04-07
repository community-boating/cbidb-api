package PDFBox.Reports.JpClassRoster

import java.awt.Color

import PDFBox.Abstract.AbstractTable
import PDFBox.PDFReport
import PDFBox.Reports.JpClassRoster.Model.{JpClassRosterModel, JpRosterData, JpSignupNotesData, JpSpecialNeedsData}
import PDFBox.Reports.JpClassRoster.View.{JpClassRosterView, JpRosterTitle, JpSignupNotesView, JpSpecialNeedsView}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.{PDFont, PDType1Font}

class JpClassRoster(data: JpClassRosterModel) extends PDFReport(data) {
  val defaultFont: PDFont = PDType1Font.HELVETICA
  val defaultBoldFont: PDFont = PDType1Font.HELVETICA_BOLD
  val defaultFontSize: Float = 13f
  val defaultColor: Color = Color.BLACK

  val sideMargin: Float = 50
  val topMargin: Float = 40

  val marginBetweenReports: Float = 10f
  val verticalLimit: Float = PDFReport.MAX_HEIGHT - (2 * topMargin)

  val left = sideMargin
  val top = PDFReport.MAX_HEIGHT - topMargin

  def appendToDocument(document: PDDocument): Unit = {
    val specNeedsFilter: (JpRosterData => Boolean) = rd => {
      rd.allergies.isDefined || rd.medications.isDefined || rd.specialNeeds.isDefined
    }
    val signupNotesFilter: (JpRosterData => Boolean) = _.signupNote.isDefined
    def rosterdataToSpecialNeedsData(rd: JpRosterData): JpSpecialNeedsData = new JpSpecialNeedsData(rd)
    def rosterdataToSignupNotesData(rd: JpRosterData): JpSignupNotesData= new JpSignupNotesData(rd)
    val fullWidthTables = List(
      new JpClassRosterView(data, data.rosterData.filter(!_.isWaitList), defaultBoldFont, defaultFont, defaultFontSize, "ENROLLMENTS"),
      new JpClassRosterView(data, data.rosterData.filter(_.isWaitList), defaultBoldFont, defaultFont, defaultFontSize, "WAIT LISTS"),
      new JpSpecialNeedsView(data.rosterData.filter(specNeedsFilter).map(rosterdataToSpecialNeedsData), defaultBoldFont, defaultFont, defaultFontSize),
      new JpSignupNotesView(data.rosterData.filter(signupNotesFilter).map(rosterdataToSignupNotesData), defaultBoldFont, defaultFont, defaultFontSize)
    )

    val firstPage = newPage(document, PDRectangle.LETTER)

    val title = new JpRosterTitle(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)

    title.draw(firstPage, left, top)
    val pageAndRemainingHeight = AbstractTable.doTable(fullWidthTables, List.empty, Some(PDFReport.MAX_HEIGHT - (title.dateTable.height + (topMargin + marginBetweenReports))), Some(firstPage), left, top, marginBetweenReports, topMargin, verticalLimit, newPage, document)
    pageAndRemainingHeight._1.get.close()
  }
}