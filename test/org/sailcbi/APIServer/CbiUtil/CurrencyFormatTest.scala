package org.sailcbi.APIServer.CbiUtil

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CurrencyFormatTest extends FunSuite {
  test("DoubleCents") {
    assert(CurrencyFormat.withCents(123.45) == "$123.45")
  }
  test("DoubleCentsRound") {
    assert(CurrencyFormat.withCents(123.4) == "$123.40")
  }
  test("DoubleCentsRound2") {
    assert(CurrencyFormat.withCents(123d) == "$123.00")
  }
  test("CommasDouble") {
    assert(CurrencyFormat.withCents(123456789.1) == "$123,456,789.10")
  }
  test("CommasDouble2") {
    assert(CurrencyFormat.withCents(7123456789d) == "$7,123,456,789.00")
  }
  test("int") {
    assert(CurrencyFormat.int(1234) == "$1,234")
  }
  test("intNoCommas") {
    assert(CurrencyFormat.int(1234, commas = false) == "$1234")
  }
  test("intAddZeroes") {
    assert(CurrencyFormat.int(1234, addZeroes = true) == "$1,234.00")
  }
}
