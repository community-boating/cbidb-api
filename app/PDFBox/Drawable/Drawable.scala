package PDFBox.Drawable

import PDFBox.ContentStreamDecorator

abstract class Drawable {
  def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit
}
