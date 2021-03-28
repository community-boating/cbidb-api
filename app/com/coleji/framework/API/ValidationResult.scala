package com.coleji.framework.API

sealed abstract class ValidationResult {
	def flatMap(f: ValidationResult => ValidationResult): ValidationResult
	def map(f: ValidationResult => ValidationResult): ValidationResult
	def combine(vr: ValidationResult): ValidationResult
	def isOk: Boolean
	def toEither: Either[List[String], Unit]
	def append(newError: String): ValidationError
}

case class ValidationError(errors: List[String]) extends ValidationResult {
	def toResultError: ResultError = ResultError(
		code="validation_error",
		message=errors.reverse.map(_.replace('\n', ' ')).mkString("\\n")
	)
	override def append(newError: String) = ValidationError(newError :: errors)
	override def combine(vr: ValidationResult): ValidationResult = vr match {
		case ve: ValidationError => ValidationError(ve.errors ::: errors)
		case _ => this
	}

	override def flatMap(f: ValidationResult => ValidationResult): ValidationResult = this
	override def map(f: ValidationResult => ValidationResult): ValidationResult = this
	override def isOk = false
	override def toEither: Either[List[String], Unit] = Left(errors)
}

case object ValidationOk extends ValidationResult {
	override def append(newError: String) = ValidationError(List(newError))
	override def flatMap(f: ValidationResult => ValidationResult): ValidationResult = f(this)
	override def map(f: ValidationResult => ValidationResult): ValidationResult = f(this)
	override def combine(vr: ValidationResult): ValidationResult = vr
	override def isOk = true
	override def toEither: Either[List[String], Unit] = Right()
}

object ValidationResult {
	def from(error: String) = ValidationError(List(error))
	def combine(errors: List[ValidationResult]): ValidationResult = {
		if (errors.isEmpty) ValidationOk
		else errors.reduce(_.combine(_))
	}
	def checkBlank(value: Option[_], name: String): ValidationResult = {
		value match {
			case Some(_) => ValidationOk
			case None => ValidationResult.from(s"${name} may not be blank.")
		}
	}
	def checkBlankCustom(value: Option[_], errorString: String): ValidationResult = {
		value match {
			case Some(_) => ValidationOk
			case None => ValidationResult.from(errorString)
		}
	}
	def inline[T](value: T)(f: T => Boolean, errorString: String): ValidationResult = {
		val ret = f(value)
		if (ret) {
			ValidationOk
		} else {
			ValidationResult.from(errorString)
		}
	}
}
