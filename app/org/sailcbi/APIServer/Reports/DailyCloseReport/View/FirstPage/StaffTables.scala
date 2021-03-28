package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FirstPage

import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.DailyCloseReportModel

class StaffTables(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val width = 150f

	def makeTable(title: String, people: List[String]): (DrawableTable, DrawableTable) = {
		val maxPeople = 4
		val header = DrawableTable(
			List(List(title)),
			List(width),
			List(ALIGN_CENTER),
			defaultBoldFont, defaultFontSize - 2
		)

		val peopleToDisplay = {
			if (people.length <= maxPeople) people
			else people.take(maxPeople - 1) :+ "(more)"
		}

		val body = DrawableTable(
			peopleToDisplay.map(s => List(s)),
			List(width),
			List(ALIGN_LEFT),
			defaultFont, defaultFontSize - 2
		)

		(header, body)
	}

	val open = makeTable("OPENING STAFF", data.staff.filter(!_.isClose).map(s => s.firstName + " " + s.lastName))
	val close = makeTable("CLOSING STAFF", data.staff.filter(_.isClose).map(s => s.firstName + " " + s.lastName))
	val sup = makeTable("SUPERVISOR", data.closeProps.finalizedBy match {
		case Some(s) => List(s)
		case None => List.empty
	})

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		open._1.draw(contentStreamDecorator, left, top)
		open._2.draw(contentStreamDecorator, left, top - open._1.height)

		close._1.draw(contentStreamDecorator, left + 180, top)
		close._2.draw(contentStreamDecorator, left + 180, top - open._1.height)

		sup._1.draw(contentStreamDecorator, left + 360, top)
		sup._2.draw(contentStreamDecorator, left + 360, top - open._1.height)
	}
}
