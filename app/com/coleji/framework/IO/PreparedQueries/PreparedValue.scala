package com.coleji.framework.IO.PreparedQueries

import com.coleji.framework.Util.DateUtil

import java.sql.PreparedStatement
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

sealed abstract class PreparedValue {
	def set(ps: PreparedStatement)(index: Int): Unit
}

case class PreparedString(s: String) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = ps.setString(index, s)
}
case class PreparedInt(i: Int) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = ps.setString(index, i.toString)
}
case class PreparedDouble(d: Double) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = ps.setString(index, d.toString)
}
case class PreparedBoolean(b: Boolean) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = ps.setString(index, if (b) "Y" else "N")
}
case class PreparedLocalDate(ld: LocalDate) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = {
		val sqlDate = java.sql.Date.valueOf(ld)
		ps.setObject(index, sqlDate)
	}
}
case class PreparedLocalDateTime(ldt: LocalDateTime) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = {
		val sqlTime = new java.sql.Timestamp(DateUtil.toBostonTime(ldt).toInstant.toEpochMilli)
		ps.setObject(index, sqlTime)
	}
}
case class PreparedZonedDateTime(zdt: ZonedDateTime) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = {
		val sqlTime = new java.sql.Timestamp(zdt.toInstant.toEpochMilli)
		ps.setObject(index, sqlTime)
	}
}
case class PreparedStringOption(o: Option[String]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(s: String) => PreparedString(s).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedIntOption(o: Option[Int]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(i: Int) => PreparedInt(i).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedDoubleOption(o: Option[Double]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(d: Double) => PreparedDouble(d).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedBooleanOption(o: Option[Boolean]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(b: Boolean) => PreparedBoolean(b).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedLocalDateOption(o: Option[LocalDate]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(ld: LocalDate) => PreparedLocalDate(ld).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedLocalDateTimeOption(o: Option[LocalDateTime]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(ldt: LocalDateTime) => PreparedLocalDateTime(ldt).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}
case class PreparedZonedDateTimeOption(o: Option[ZonedDateTime]) extends PreparedValue {
	override def set(ps: PreparedStatement)(index: Int): Unit = o match {
		case Some(zdt: ZonedDateTime) => PreparedZonedDateTime(zdt).set(ps)(index)
		case None => ps.setObject(index, null)
	}
}

object PreparedValue {
	implicit def wrapString(s: String): PreparedString = PreparedString(s)
	implicit def wrapInt(i: Int): PreparedInt = PreparedInt(i)
	implicit def wrapDouble(d: Double): PreparedDouble = PreparedDouble(d)
	implicit def wrapBoolean(b: Boolean): PreparedBoolean = PreparedBoolean(b)
	implicit def wrapLocalDate(ld: LocalDate): PreparedLocalDate = PreparedLocalDate(ld)
	implicit def wrapLocalDateTime(ldt: LocalDateTime): PreparedLocalDateTime = PreparedLocalDateTime(ldt)
	implicit def wrapZonedDateTime(zdt: ZonedDateTime): PreparedZonedDateTime = PreparedZonedDateTime(zdt)

	implicit def wrapStringOption(s: Option[String]): PreparedStringOption = PreparedStringOption(s)
	implicit def wrapIntOption(i: Option[Int]): PreparedIntOption = PreparedIntOption(i)
	implicit def wrapDoubleOption(d: Option[Double]): PreparedDoubleOption = PreparedDoubleOption(d)
	implicit def wrapBooleanOption(b: Option[Boolean]): PreparedBooleanOption = PreparedBooleanOption(b)
	implicit def wrapLocalDateOption(ld: Option[LocalDate]): PreparedLocalDateOption = PreparedLocalDateOption(ld)
	implicit def wrapLocalDateTimeOption(ldt: Option[LocalDateTime]): PreparedLocalDateTimeOption = PreparedLocalDateTimeOption(ldt)
	implicit def wrapZonedDateTimeOption(zdt: Option[ZonedDateTime]): PreparedZonedDateTimeOption = PreparedZonedDateTimeOption(zdt)
}