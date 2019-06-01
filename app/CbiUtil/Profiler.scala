package CbiUtil

class Profiler {
	private var stamp: Long = System.currentTimeMillis

	def lap(s: String): Unit = {
		val now = System.currentTimeMillis
		println(s + ": " + (now - stamp))
		stamp = now
	}
}
