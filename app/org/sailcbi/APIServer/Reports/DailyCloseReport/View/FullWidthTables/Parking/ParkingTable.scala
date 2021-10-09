package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.Parking

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, DrawableTable, MultiDrawableTable}
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.ParkingData

class ParkingTable(
						  data: ParkingData,
						  headerFont: PDFont,
						  bodyFont: PDFont,
						  fontSize: Float
				  ) extends AbstractTable[ParkingData](
	List(data),
	new MultiDrawableTable(List(DrawableTable(
		List(List("PARKING TICKETS")),
		List(325f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("# Open", "# Close", "+ / -", "# Comp", "# Sold", "$ Sold")),
		List(50f, 50f, 50f, 50f, 50f, 75f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)