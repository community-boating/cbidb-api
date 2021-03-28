package com.coleji.framework.Exception

class UnauthorizedAccessException(
	private val message: String = "Unauthorized Access Denied",
	private val cause: Throwable = null
) extends Exception(message, cause)