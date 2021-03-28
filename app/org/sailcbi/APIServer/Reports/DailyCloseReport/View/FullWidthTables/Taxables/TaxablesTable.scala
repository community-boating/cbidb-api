package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.Taxables

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable._
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.TaxablesItem

class TaxablesTable(
						   items: List[TaxablesItem], defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float
				   ) extends AbstractTable[TaxablesItem](
	items,
	new MultiDrawableTable(List(DrawableTable(
		List(List("TAXABLES")),
		List(500f),
		List(ALIGN_CENTER),
		defaultBoldFont,
		defaultFontSize,
		1,
		3
	), DrawableTable(
		List(List("Item Name", "Discount", "# Sold", "Pretax", "Tax Amt", "Total")),
		List(120f, 120f, 65f, 65f, 65f, 65f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		defaultBoldFont,
		defaultFontSize,
		1,
		3
	))),
	new MultiDrawableTable(List(DrawableTable(
		List(List("Totals:", items.map(_.pretax).sum.format(), items.map(_.taxAmount).sum.format(), items.map(i => i.pretax + i.taxAmount).sum.format())),
		List(305f, 65f, 65f, 65f),
		List(ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
		defaultBoldFont,
		defaultFontSize,
		1,
		3
	))),
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
	defaultFont,
	defaultFontSize,
	1,
	3
)