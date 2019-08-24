package org.sailcbi.APIServer.Api

case class ValidationError(errors: List[String]) {
	def toResultError(): ResultError = ResultError(
		code="validation_error",
		message=errors.map(_.replace('\n', ' ')).mkString("\\n")
	)
	def append(newError: String) = ValidationError(newError :: errors)
	def combine(ve: ValidationError) = ValidationError(ve.errors ::: errors)
}

object ValidationError {
	def from(error: String) = ValidationError(List(error))
	def combine(errors: List[ValidationError]): Option[ValidationError] = {
		if (errors.isEmpty) None
		else Some(errors.reduce(_.combine(_)))
	}
	def checkBlank(value: Option[_], name: String): Option[ValidationError] = {
		value match {
			case Some(_) => None
			case None => Some(ValidationError.from(s"${name} may not be blank."))
		}
	}
	def checkBlankCustom(value: Option[_], errorString: String): Option[ValidationError] = {
		value match {
			case Some(_) => None
			case None => Some(ValidationError.from(errorString))
		}
	}
	def inline[T](value: T)(f: T => Boolean, errorString: String): Option[ValidationError] = {
		val ret = f(value)
		if (ret) {
			None
		} else {
			Some(ValidationError.from(errorString))
		}
	}
}
