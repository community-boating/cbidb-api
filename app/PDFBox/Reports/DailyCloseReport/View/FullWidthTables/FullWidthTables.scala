package PDFBox.Reports.DailyCloseReport.View.FullWidthTables

import java.awt.Color

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable.MultiPageDrawable
import PDFBox.Reports.DailyCloseReport.Model._
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.APClasses.APClassTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.APVouchers.APVouchersTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Donations.DonationsTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.GCComps.GCCompsTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.GCRedemptions.GCRedemptionsTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.GCSales.GCSalesTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.JPClasses.JPClassTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Memberships.MembershipsTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Parking.ParkingTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Retail.RetailTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Summary.SummaryTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Taxables.TaxablesTable
import PDFBox.Reports.DailyCloseReport.View.FullWidthTables.WaiversPrivsCards._
import PDFBox.{ContentStreamDecorator, PDFReport}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont

class FullWidthTables(data: DailyCloseReportModel, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, defaultColor: Color, left: Float, top: Float, topMargin: Float) extends MultiPageDrawable {
	val marginBetweenReports: Float = 10f
	val verticalLimit: Float = PDFReport.MAX_HEIGHT - (2 * topMargin)
	val summaryTable = new SummaryTable(
		data.summaryItems,
		defaultFont,
		defaultBoldFont,
		defaultFontSize
	)
	val taxablesTable = new TaxablesTable(
		data.taxables,
		defaultFont,
		defaultBoldFont,
		defaultFontSize
	)
	val membershipsTable = new MembershipsTable(
		data.membershipSales,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val donationsTable = new DonationsTable(
		data.donations,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	val jpClassesTable = new JPClassTable(
		data.jpSignups,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val apClassesTable = new APClassTable(
		data.apSignups,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	val apVouchersTable = new APVouchersTable(
		data.apVouchers,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	val retailTable = new RetailTable(
		data.retail,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	val parkingTable = new ParkingTable(
		data.parking,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val gcSalesTable = new GCSalesTable(
		data.gcSales,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val gcCompsTable = new GCCompsTable(
		data.gcComps,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	val gcRedemptionsTable = new GCRedemptionsTable(
		data.gcRedemptions,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val waiversPrivsTable = new WaiversPrivsTable(
		data.waiversAndPrivs,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)
	val replacementCardsTable = new ReplacementCardsTable(
		data.replacementCards,
		defaultBoldFont,
		defaultFont,
		defaultFontSize
	)

	def draw(document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator): Unit = {
		val fullWidthTables = List(
			summaryTable,
			taxablesTable,
			membershipsTable,
			donationsTable,
			jpClassesTable,
			apClassesTable,
			apVouchersTable,
			retailTable,
			parkingTable,
			gcSalesTable,
			gcCompsTable,
			gcRedemptionsTable
		)

		val page = AbstractTable.doTable(fullWidthTables, List.empty, Some(0), None, left, top, marginBetweenReports, topMargin, verticalLimit, newPage, document)._1
		if (page.isDefined) page.get.close()

		val waiversPrivsDrawables = waiversPrivsTable.getDrawables(PDFReport.MAX_HEIGHT - 50, None, None)
		val replacementCardsDrawables = replacementCardsTable.getDrawables(PDFReport.MAX_HEIGHT - 50, None, None)
		val numberPages = if (waiversPrivsDrawables.length > replacementCardsDrawables.length) {
			waiversPrivsDrawables.length
		} else {
			replacementCardsDrawables.length
		}
		0.until(numberPages).foreach(i => {
			val page = newPage(document, PDRectangle.LETTER)
			if (waiversPrivsDrawables.length > i) waiversPrivsDrawables(i).draw(page, left, top)
			if (replacementCardsDrawables.length > i) replacementCardsDrawables(i).draw(page, left + 300, top)
			page.close()
		})
	}
}
