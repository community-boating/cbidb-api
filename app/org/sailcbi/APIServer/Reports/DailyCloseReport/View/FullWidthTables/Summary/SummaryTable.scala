package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.Summary

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.SummaryItem

class SummaryTable(
						  items: List[SummaryItem], defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float
				  ) extends AbstractTable[SummaryItem](
	items,
	new MultiDrawableTable(List(DrawableTable(
		List(List("SUMMARY")),
		List(515f),
		List(ALIGN_CENTER),
		defaultBoldFont,
		defaultFontSize,
		1,
		3
	), DrawableTable(
		List(List("Item Name", "Discount", "# Inperson Sold", "Inperson Total", "# Online Sold", "Online Total")),
		List(100f, 100f, 65f, 100f, 50f, 100f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		defaultBoldFont,
		defaultFontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
	defaultFont,
	defaultFontSize,
	1,
	3
)