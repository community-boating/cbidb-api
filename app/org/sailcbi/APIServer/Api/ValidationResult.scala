package org.sailcbi.APIServer.Api

sealed abstract class ValidationResult {
	def flatMap(f: ValidationResult => ValidationResult): ValidationResult
	def map(f: ValidationResult => ValidationResult): ValidationResult
	def combine(vr: ValidationResult): ValidationResult
	def isOk: Boolean
}

case class ValidationError(errors: List[String]) extends ValidationResult {
	def toResultError: ResultError = ResultError(
		code="validation_error",
		message=errors.map(_.replace('\n', ' ')).mkString("\\n")
	)
	def append(newError: String) = ValidationError(newError :: errors)
	def combine(vr: ValidationResult): ValidationResult = vr match {
		case ve: ValidationError => ValidationError(ve.errors ::: errors)
		case _ => this
	}

	def flatMap(f: ValidationResult => ValidationResult): ValidationResult = this
	def map(f: ValidationResult => ValidationResult): ValidationResult = this
	def isOk = false
}

case object ValidationOk extends ValidationResult {
	def flatMap(f: ValidationResult => ValidationResult): ValidationResult = f(this)
	def map(f: ValidationResult => ValidationResult): ValidationResult = f(this)
	def combine(vr: ValidationResult): ValidationResult = vr
	def isOk = true
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
