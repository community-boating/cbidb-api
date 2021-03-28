package com.coleji.framework.Util

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

	// TODO: nulls sorting in the correct placement?
	def sort(a: Option[Int], b: Option[Int]): CascadeSort = v match {
		case Some(x) => new CascadeSort(Some(x))
		case None => {
			a match {
				case Some(ai: Int) => b match {
					case Some(bi: Int) =>
						if (ai < bi) new CascadeSort(Some(true))
						else if (ai > bi) new CascadeSort(Some(false))
						else new CascadeSort(None)
					case None => new CascadeSort(Some(true))
				}
				case None => b match {
					case Some(bi: Int) => new CascadeSort(Some(false))
					case None => new CascadeSort(None)
				}
			}
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
			if (a isBefore b) new CascadeSort(Some(true))
			else if (a isAfter b) new CascadeSort(Some(false))
			else new CascadeSort(None)
		}
	}

	def end(pref: Boolean = true): Boolean = v match {
		case Some(x) => x
		case None => pref
	}
}

object CascadeSort {
	def sort(a: Int, b: Int): CascadeSort = new CascadeSort(None).sort(a, b)

	def sort(a: String, b: String): CascadeSort = new CascadeSort(None).sort(a, b)

	def sort(a: LocalDate, b: LocalDate): CascadeSort = new CascadeSort(None).sort(a, b)

	def sort(a: LocalDateTime, b: LocalDateTime): CascadeSort = new CascadeSort(None).sort(a, b)
}