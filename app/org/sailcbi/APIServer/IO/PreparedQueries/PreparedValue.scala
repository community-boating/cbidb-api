package org.sailcbi.APIServer.IO.PreparedQueries

import java.sql.PreparedStatement
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

import org.sailcbi.APIServer.CbiUtil.DateUtil

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