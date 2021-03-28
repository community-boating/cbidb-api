package com.coleji.framework.PDFBox.Abstract

import com.coleji.framework.PDFBox.ContentStreamDecorator
import com.coleji.framework.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont

class AbstractTable[T <: RowData](
	data: List[T],
	header: MultiDrawableTable,
	footer: MultiDrawableTable,
	bodyAligns: List[Alignment],
	bodyFont: PDFont,
	fontSize: Float,
	thickness: Int,
	padding: Int
) {
	// TODO: Assume the last header row has all the widths.  Not a safe assumption
	val widths: List[Float] = header.tables.last.rows.head.cells.map(_.width)
	lazy val rows: List[DrawableRow] = data.map(_.asRow(widths, bodyAligns, bodyFont, fontSize, thickness, padding))

	def replaceLastWithOverflow(t: MultiDrawableTable): MultiDrawableTable = {
		// TODO: what if footer?
		val rows = t.tables.last.rows
		new MultiDrawableTable(List(
			new DrawableTable(rows.take(rows.length - 1), thickness),
			AbstractTable.overflowRow(widths.sum, bodyFont, fontSize, thickness, padding)
		))
	}

	def getDrawables(verticalConstraint: Float, initialVerticalConstraint: Option[Float] = None, drawablesLimit: Option[Int] = None): List[MultiDrawableTable] = {
		def recurse(recurseRows: List[DrawableRow], thisVerticalConstraint: Float): List[MultiDrawableTable] = {
			type RowWithHeight = (DrawableRow, Float)
			val headersWithHeight: List[RowWithHeight] = header.tables.flatMap(_.rows).map(r => (r, r.height))
			val rowsWithHeights: List[RowWithHeight] = recurseRows zip recurseRows.map(_.height)
			// .tail because we dont care about the one that is just the header with no rows
			val splits: List[List[RowWithHeight]] = rowsWithHeights.scanLeft(headersWithHeight: List[RowWithHeight])((list, rowWithHeight) => list :+ rowWithHeight).tail
			val remainders: List[List[RowWithHeight]] = rowsWithHeights.scanRight(List.empty: List[RowWithHeight])((rowWithHeight, list) => rowWithHeight :: list).tail
			val splitsWithRemainders: List[(List[RowWithHeight], List[RowWithHeight])] = splits zip remainders
			val fittingSplits: List[(List[RowWithHeight], List[RowWithHeight])] = splitsWithRemainders.takeWhile(t => t._1.map(_._2).sum <= thisVerticalConstraint)
			if (fittingSplits.isEmpty) {
				// No way to make this fit.  If this was a first section with a smaller constraint, try again.
				// If this was a full height section, you're fucked.  This thing just won't fit
				if (thisVerticalConstraint < verticalConstraint) new MultiDrawableTable(List(new DrawableTable(List.empty, thickness))) :: recurse(recurseRows, verticalConstraint)
				else List.empty
			} else {
				val winningSplit: (List[RowWithHeight], List[RowWithHeight]) = fittingSplits.last
				new MultiDrawableTable(List(new DrawableTable(winningSplit._1.map(_._1), thickness))) :: recurse(winningSplit._2.map(_._1), verticalConstraint)
			}
		}

		val ret = recurse(rows ::: footer.tables.flatMap(_.rows), initialVerticalConstraint.getOrElse(verticalConstraint))
		if (rows.isEmpty) {
			List(new MultiDrawableTable(header.tables :+ new DrawableTable(List(AbstractTable.noDataFoundRow(widths.sum, bodyFont, fontSize, thickness, padding)), thickness)))
		} else if (drawablesLimit.isEmpty || ret.length <= drawablesLimit.get) ret
		else {
			val lim = drawablesLimit.get
			ret.take(lim - 1) :+ replaceLastWithOverflow(ret(lim - 1))
		}
	}
}

object AbstractTable {
	def noDataFoundRow(width: Float, font: PDFont, fontSize: Float, thickness: Int, padding: Int): DrawableRow =
		DrawableRow(List(("No data.", width, ALIGN_CENTER)), font, fontSize, thickness, padding)

	def overflowRow(width: Float, font: PDFont, fontSize: Float, thickness: Int, padding: Int): DrawableTable =
		new DrawableTable(List(DrawableRow(List(("(...)", width, ALIGN_CENTER)), font, fontSize, thickness, padding)), thickness)

	// Given a bunch of abstract tables,
	// turn them into lists of concrete drawables that use page space as efficiently as possible
	// and render, closing and creating new pages as necessary
	// TODO: could decouple the logic of figuring out how many pages we need and which abstracts share a page with each other, from actually drawing
	def doTable(
					   abstractTables: List[AbstractTable[_]],
					   drawables: List[MultiDrawableTable],
					   remainingHeight: Option[Float],
					   currentPage: Option[ContentStreamDecorator],
					   left: Float,
					   top: Float,
					   marginBetweenReports: Float,
					   topMargin: Float,
					   verticalLimit: Float,
					   newPage: (PDDocument, PDRectangle) => ContentStreamDecorator,
					   document: PDDocument
			   ): (Option[ContentStreamDecorator], Option[Float]) = {
		if (drawables.isEmpty) {
			if (abstractTables.isEmpty) (currentPage, remainingHeight) //we're done
			else {
				val pageAndRemainingHeight: (ContentStreamDecorator, Option[Float]) = currentPage match {
					case Some(p) => (p, Some(remainingHeight.get - (marginBetweenReports + topMargin)))
					case None => (newPage(document, PDRectangle.LETTER), None)
				}
				doTable(
					abstractTables.tail,
					abstractTables.head.getDrawables(
						verticalLimit,
						pageAndRemainingHeight._2,
						None
					),
					Some(pageAndRemainingHeight._2.getOrElse(verticalLimit)),
					Some(pageAndRemainingHeight._1),
					left,
					top,
					marginBetweenReports,
					topMargin,
					verticalLimit,
					newPage,
					document
				)
			}
		}
		else if (currentPage.isDefined && drawables.head.height <= remainingHeight.get) {
			// squeeze this drawable on the current page
			drawables.head.draw(currentPage.get, left, remainingHeight.get + topMargin)
			doTable(
				abstractTables,
				drawables.tail,
				Some(remainingHeight.get - drawables.head.height),
				currentPage,
				left,
				top,
				marginBetweenReports,
				topMargin,
				verticalLimit,
				newPage,
				document
			)
		} else {
			// Time for a new page.  If there's an active page, close it
			if (currentPage.isDefined) currentPage.get.close()
			val page: ContentStreamDecorator = newPage(document, PDRectangle.LETTER)
			drawables.head.draw(page, left, top)
			doTable(
				abstractTables,
				drawables.tail,
				Some(verticalLimit - drawables.head.height),
				Some(page),
				left,
				top,
				marginBetweenReports,
				topMargin,
				verticalLimit,
				newPage,
				document
			)
		}
	}
}