import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}

def a(): ValidationResult = {
	println("running a")
	ValidationOk
}

def b(): ValidationResult = {
	println("running b")
	ValidationOk
}

def c(): ValidationResult = {
	println("running c")
	ValidationError.apply(List("didnt work"))
}

def d(): ValidationResult = {
	println("running d")
	ValidationOk

}

val finalResult = for {
	_ <- a()
	_ <- b()
	_ <- c()
	res <- d()
} yield res

println("Final result:" + finalResult)

// Output
//
//running a
//running b
//running c
//Final result:ValidationError(List(didnt work))