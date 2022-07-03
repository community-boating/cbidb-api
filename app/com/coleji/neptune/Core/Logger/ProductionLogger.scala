package com.coleji.neptune.Core.Logger

import com.coleji.neptune.Core.Emailer.Emailer
import com.coleji.neptune.Core.PermissionsAuthority

class ProductionLogger private[Core](emailer: Emailer)(implicit PA: PermissionsAuthority) extends Logger {
	private val emailTo = PA.systemParams.emailCrashesTo

	override def trace(s: String): Unit = println(s)

	override def trace(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	override def info(s: String): Unit = println(s)

	override def info(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	override def warning(s: String): Unit = {
		emailer.send("CBI API Warning" + getSubjectSuffix, s, emailTo)
		println(s)
	}

	override def warning(s: String, e: Throwable): Unit = {
		val msg = s + "\n" + prettyPrintException(e)
		emailer.send("CBI API Warning" + getSubjectSuffix, msg, emailTo)
		println(msg)
	}

	override def error(s: String): Unit = {
		emailer.send("CBI API Error" + getSubjectSuffix, s, emailTo)
		println(s)
	}

	override def error(s: String, e: Throwable): Unit = {
		val msg = s + "\n" + prettyPrintException(e)
		emailer.send("CBI API Error" + getSubjectSuffix, msg, emailTo)
		println(msg)
	}

	private def getRandomString(numberLetters: Int = 5): String = scala.util.Random.alphanumeric.take(numberLetters).mkString

	private def getSubjectSuffix: String = " (" + getRandomString() + ") (" + PA.instanceName + ")"
}
