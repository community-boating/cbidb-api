package Api

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
}
