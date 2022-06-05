import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}

def a(): Either[ValidationResult, Int] = {
	println("running a")
	Right(1)
}

def b(): Either[ValidationResult, Int] = {
	println("running b")
	Left(ValidationOk)
}

def c(): Either[ValidationResult, Int] = {
	println("running c")
	Left(ValidationError.apply(List("didnt work")))
}

def d(): Either[ValidationResult, Int] = {
	println("running d")
	Right(4)
}

val finalResult = for {
	_ <- a()
	_ <- b()
	_ <- c()
	res <- d()
} yield res

println("Final result:" + finalResult)

// Output:
//
//running a
//running b
//Final result:Left(ValidationOk)

