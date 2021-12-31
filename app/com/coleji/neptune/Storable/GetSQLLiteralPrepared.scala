package com.coleji.neptune.Storable

/**
 * For use with Prepared Statements only. Do not inject these directly into SQL!
 */
object GetSQLLiteralPrepared {
	def apply(s: String): String = if (s == "") null else s

	def apply(s: Option[String]): String = s match {
		case Some(x) => apply(x);
		case None => null
	}

	def apply(b: Boolean): String = if (b) "Y" else "N"
}
