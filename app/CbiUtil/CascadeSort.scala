package CbiUtil

import java.time.{LocalDate, LocalDateTime}

class CascadeSort(v: Option[Boolean]) {
  def sort(a: Int, b: Int): CascadeSort = v match {
    case Some(x) => new CascadeSort(Some(x))
    case None => {
      if (a < b) new CascadeSort(Some(true))
      else if (a > b) new CascadeSort(Some(false))
      else new CascadeSort(None)
    }
  }

  def sort(a: String, b: String): CascadeSort = v match {
    case Some(x) => new CascadeSort(Some(x))
    case None => {
      if (a < b) new CascadeSort(Some(true))
      else if (a > b) new CascadeSort(Some(false))
      else new CascadeSort(None)
    }
  }

  def sort(a: LocalDate, b: LocalDate): CascadeSort = v match {
    case Some(x) => new CascadeSort(Some(x))
    case None => {
      if (a isBefore b) new CascadeSort(Some(true))
      else if (a isAfter b) new CascadeSort(Some(false))
      else new CascadeSort(None)
    }
  }

  def sort(a: LocalDateTime, b: LocalDateTime): CascadeSort = v match {
    case Some(x) => new CascadeSort(Some(x))
    case None => {
      if (a isBefore  b) new CascadeSort(Some(true))
      else if (a isAfter b) new CascadeSort(Some(false))
      else new CascadeSort(None)
    }
  }

  def end(pref: Boolean = true): Boolean = v match {
    case Some(x) => x
    case None => pref
  }
}

object CascadeSort{
  def sort(a: Int, b: Int): CascadeSort = new CascadeSort(None).sort(a, b)
  def sort(a: String, b: String): CascadeSort = new CascadeSort(None).sort(a, b)
  def sort(a: LocalDate, b: LocalDate): CascadeSort = new CascadeSort(None).sort(a, b)
  def sort(a: LocalDateTime, b: LocalDateTime): CascadeSort = new CascadeSort(None).sort(a, b)
}