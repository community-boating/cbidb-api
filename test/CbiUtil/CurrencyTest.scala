package CbiUtil

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CurrencyTest extends FunSuite {
  // Formats
  test("DollarsDouble") {
    assert(Currency.dollars(123.45).format() == "$123.45")
  }
  test("DollarsInt") {
    assert(Currency.dollars(123).format() == "$123.00")
  }
  test("CentsNoZeroes") {
    assert(Currency.cents(123400).format(zeroes = false) == "$1,234")
  }
  test("DollarsDoubleNoCommas") {
    assert(Currency.dollars(12345.6).format(commas = false) == "$12345.60")
  }

  // Arithmetic
  test("add") {
    val c1: Currency = Currency.cents(123)
    val c2: Currency = Currency.cents(456)
    assert((c1 + c2).cents == 579)
  }
  test("sum") {
    val c1: Currency = Currency.cents(123)
    val c2: Currency = Currency.cents(456)
    assert(List(c1, c2).sum.cents == 579)
  }
  test("max") {
    val c1: Currency = Currency.cents(123)
    val c2: Currency = Currency.cents(456)
    assert(List(c1, c2).max.cents == 456)
  }
}