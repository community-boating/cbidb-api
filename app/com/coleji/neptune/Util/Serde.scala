package com.coleji.neptune.Util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.util.Base64

object Serde {
	def serializeStandard[T](result: T): String = {
		val baos = new ByteArrayOutputStream
		val oos = new ObjectOutputStream(baos)
		oos.writeObject(result)
		oos.close()
		Base64.getEncoder.encodeToString(baos.toByteArray)
	}

	def deseralizeStandard[T](resultString: String): T = {
		val data = Base64.getDecoder.decode(resultString)
		val ois = new ObjectInputStream(new ByteArrayInputStream(data))
		val o = ois.readObject
		ois.close()
		o.asInstanceOf[T]
	}

	/**
	 * Seralizing lists doesn't seem to always work properly.  Use this to serialize each element individually, then join with :
	 */
	def serializeList[T](result: List[T]): String = {
		result.map(serializeStandard).mkString(":")
	}

	def deserializeList[T](resultString: String): List[T] = {
		resultString.split(":").toList.map(deseralizeStandard[T])
	}
}
