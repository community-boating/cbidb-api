package org.sailcbi.APIServer.Reports.JpClassRoster

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.PDFReport
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.{PDFont, PDType1Font, Standard14Fonts}
import org.sailcbi.APIServer.Reports.JpClassRoster.Model.{JpClassRosterModel, JpRosterData, JpSignupNotesData, JpSpecialNeedsData}
import org.sailcbi.APIServer.Reports.JpClassRoster.View.{JpClassRosterView, JpRosterTitle, JpSignupNotesView, JpSpecialNeedsView}

import java.awt.Color

class JpClassRoster(data: JpClassRosterModel) extends PDFReport(data) {
	val defaultFont: PDFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA)
	val defaultBoldFont: PDFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
	val defaultFontSize: Float = 10f
	val defaultColor: Color = Color.BLACK

	val sideMargin: Float = 30
	val topMargin: Float = 40

	val marginBetweenReports: Float = 10f
	val verticalLimit: Float = PDFReport.MAX_HEIGHT - (2 * topMargin)

	val left = sideMargin
	val top = PDFReport.MAX_HEIGHT - topMargin

	def appendToDocument(document: PDDocument): Unit = {
		val WAIT_LIST_SECTION = "(Wait List)"
		val DEFAULT_SECTION = "(Default Section)"

		val specNeedsFilter: (JpRosterData => Boolean) = rd => {
			rd.allergies.isDefined || rd.medications.isDefined || rd.specialNeeds.isDefined
		}
		val signupNotesFilter: (JpRosterData => Boolean) = _.signupNote.isDefined

		def rosterdataToSpecialNeedsData(rd: JpRosterData): JpSpecialNeedsData = new JpSpecialNeedsData(rd)

		def rosterdataToSignupNotesData(rd: JpRosterData): JpSignupNotesData = new JpSignupNotesData(rd)

		val rosterBySection = data.rosterData.groupBy(rd => {
			if (rd.isWaitList) WAIT_LIST_SECTION
			else rd.sectionName.getOrElse(DEFAULT_SECTION)
		}).toList.sortBy(t => {
			// I'm sure there's a less shitty way to do this but w/e
			if (t._1 == WAIT_LIST_SECTION) "zzzzz"  // ensure it sorts last
			else if (t._1 == DEFAULT_SECTION) "yyyyy" // ensure it sorts last except for wait list
			else t._1.toLowerCase()
		})

		rosterBySection.foreach(Function.tupled((sectionName: String, sectionRosterData: List[JpRosterData]) => {
			val titleText = {
				if (sectionName == WAIT_LIST_SECTION || sectionName == DEFAULT_SECTION) sectionName
				else sectionName + " - ENROLLMENTS"
			}
			val fullWidthTables = List(
				new JpClassRosterView(data, sectionRosterData, defaultBoldFont, defaultFont, defaultFontSize, titleText + " - Attendance"),
				new JpSpecialNeedsView(sectionRosterData.filter(specNeedsFilter).map(rosterdataToSpecialNeedsData), defaultBoldFont, defaultFont, defaultFontSize),
				new JpSignupNotesView(sectionRosterData.filter(signupNotesFilter).map(rosterdataToSignupNotesData), defaultBoldFont, defaultFont, defaultFontSize)
			)

			val firstPage = newPage(document, PDRectangle.LETTER)

			val title = new JpRosterTitle(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)

			title.draw(firstPage, left, top)
			val pageAndRemainingHeight = AbstractTable.doTable(
				fullWidthTables,
				List.empty,
				Some(PDFReport.MAX_HEIGHT - (title.dateTable.height + (topMargin + marginBetweenReports))),
				Some(firstPage),
				left,
				top,
				marginBetweenReports,
				topMargin,
				verticalLimit,
				newPage,
				document
			)
			pageAndRemainingHeight._1.get.close()
		}))
	}
}