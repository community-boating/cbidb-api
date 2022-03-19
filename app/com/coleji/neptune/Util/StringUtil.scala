package com.coleji.neptune.Util

object StringUtil {
	/**
	 * Given a string e.g. "aaa:bbb" and a delimiter e.g. ":"
	 * return the two strings formed by splitting on the delimiter and dropping it from the second string e.g. ("aaa", "bbb")
	 * If the delimiter is not present, return (s,"")
	 */
	def splitAndDrop(s: String, c: String): (String, String) = {
		val index = s.indexOf(c)
		if (index == -1) (s,"")
		else {
			val ret = s.splitAt(index)
			(ret._1, ret._2.substring(1))
		}
	}
}
