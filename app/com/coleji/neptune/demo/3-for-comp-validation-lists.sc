import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}

def a(): List[ValidationResult] = {
	println("running a")
	List(ValidationOk, ValidationOk, ValidationOk)
}

def b(): List[ValidationResult] = {
	println("running b")
	List.empty
}

def c(): List[ValidationResult] = {
	println("running c")
	List(
		ValidationOk,
		ValidationError.apply(List("didnt work")),
		ValidationOk,
		ValidationError.apply(List("second test didnt work")),
		ValidationOk
	)
}

def d(): List[ValidationResult] = {
	println("running d")
	List(ValidationOk, ValidationError.apply(List("shouldnt get here")))

}

def e(): List[ValidationResult] = {
	println("running e")
	List(ValidationOk)

}

val finalResult = for {
	_ <- ValidationResult.combine(a())
	_ <- ValidationResult.combine(b())
	_ <- ValidationResult.combine(c())
	_ <- ValidationResult.combine(d())
	res <- ValidationResult.combine(e())
} yield res

println("Final result:" + finalResult)
finalResult match {
	case e: ValidationError => println("Error ct: " + e.errors.size)
}

// output
//
//running a
//running b
//running c
//Final result:ValidationError(List(second test didnt work, didnt work))
//Error ct: 2