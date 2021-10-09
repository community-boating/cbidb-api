package com.coleji.neptune.Util

object BitVector {
	def testBit(vector: Int, bit: Int): Boolean = (vector >> bit) % 2 != 0
}
