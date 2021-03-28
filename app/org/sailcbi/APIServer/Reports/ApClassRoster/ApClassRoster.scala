package org.sailcbi.APIServer.Reports.ApClassRoster

import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.PDFReport
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.{PDFont, PDType1Font}
import org.sailcbi.APIServer.Reports.ApClassRoster.Model.{ApClassRosterModel, ApRosterData, ApSignupNotesData, ApSpecialNeedsData}
import org.sailcbi.APIServer.Reports.ApClassRoster.View.{ApClassRosterView, ApRosterTitle, ApSignupNotesView, ApSpecialNeedsView}

import java.awt.Color

class ApClassRoster(data: ApClassRosterModel) extends PDFReport(data) {
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
		val specNeedsFilter: (ApRosterData => Boolean) = rd => {
			rd.allergies.isDefined || rd.medications.isDefined || rd.specialNeeds.isDefined
		}
		val signupNotesFilter: (ApRosterData => Boolean) = _.signupNote.isDefined

		def rosterdataToSpecialNeedsData(rd: ApRosterData): ApSpecialNeedsData = new ApSpecialNeedsData(rd)

		def rosterdataToSignupNotesData(rd: ApRosterData): ApSignupNotesData = new ApSignupNotesData(rd)

		val fullWidthTables = List(
			new ApClassRosterView(data, data.rosterData.filter(!_.isWaitList), defaultBoldFont, defaultFont, defaultFontSize, "ENROLLMENTS"),
			new ApClassRosterView(data, data.rosterData.filter(_.isWaitList), defaultBoldFont, defaultFont, defaultFontSize, "WAIT LISTS"),
			new ApSpecialNeedsView(data.rosterData.filter(specNeedsFilter).map(rosterdataToSpecialNeedsData), defaultBoldFont, defaultFont, defaultFontSize),
			new ApSignupNotesView(data.rosterData.filter(signupNotesFilter).map(rosterdataToSignupNotesData), defaultBoldFont, defaultFont, defaultFontSize)
		)

		val firstPage = newPage(document, PDRectangle.LETTER)

		val title = new ApRosterTitle(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)

		title.draw(firstPage, left, top)
		val pageAndRemainingHeight = AbstractTable.doTable(fullWidthTables, List.empty, Some(PDFReport.MAX_HEIGHT - (title.dateTable.height + (topMargin + marginBetweenReports))), Some(firstPage), left, top, marginBetweenReports, topMargin, verticalLimit, newPage, document)
		pageAndRemainingHeight._1.get.close()
	}
}