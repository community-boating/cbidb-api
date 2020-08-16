package org.sailcbi.APIServer.CbiUtil

class Profiler {
	private var stamp: Long = System.currentTimeMillis()

	def lap(s: String): Long = {
		val now = System.currentTimeMillis()
		val diff = now - stamp
		println(s + ": " + diff)
		stamp = now
		diff
	}
}

object Profiler {
//	private var runningTotals = new mutable.HashMap[String, Long]
//	private var steps: List[String] = List.empty
//	def add(name: String, time: Long): Long = {
//		runningTotals.get(name) match {
//			case None => {
//				steps = name :: steps
//				runningTotals(name) = time
//				time
//			}
//			case Some(l) => {
//				val newTotal = l + time
//				runningTotals(name) = newTotal
//				newTotal
//			}
//		}
//	}
//
//	def report(): Unit = {
//		println("*** REPORT ***")
//		val formatter = new DecimalFormat("#,###")
//		steps.reverse.foreach(s => {
//			println(s + ": " + formatter.format(runningTotals(s)))
//		})
//	}
}