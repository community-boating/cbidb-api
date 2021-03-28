package com.coleji.framework.Util

object NAStrings {
	def nullifyNAString(os: Option[String]): Option[String] = os match {
		case None => None
		case Some(s) => s.toLowerCase.trim match {
			case "na" => None
			case "n/a" => None
			case "none" => None
			case "no" => None
			case "zero" => None
			case _ => Some(s)
		}
	}
}
