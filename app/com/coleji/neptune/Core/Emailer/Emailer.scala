package com.coleji.neptune.Core.Emailer

abstract class Emailer {
	private[Core] def send(subject: String, body: String, to: String): Unit
}
