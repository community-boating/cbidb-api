package PDFBox.Drawable

import PDFBox.ContentStreamDecorator

class MultiDrawableTable(val tables: List[DrawableTable]) extends Drawable {
  def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
    var nowTop = top
    tables.foreach(t => {
      t.draw(contentStreamDecorator, left, nowTop)
      nowTop -= t.height
    })
  }

  def height: Float = tables.map(_.height).sum
}

object MultiDrawableTable {
  val empty = new MultiDrawableTable(List.empty)
}