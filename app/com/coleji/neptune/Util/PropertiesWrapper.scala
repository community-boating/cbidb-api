package com.coleji.neptune.Util

import java.io.{File, FileInputStream, IOException}
import java.util.Properties

class PropertiesWrapper(fileLocation: String, requiredProperties: List[String] = List.empty) {
	private val properties = {
		val properties = new Properties()
		val f = new File(fileLocation)
		if (!f.exists()) {
			throw new IOException("Unable to locate properties file at " + fileLocation)
		}
		val fis = new FileInputStream(f)
		properties.load(fis)
		fis.close()
		requiredProperties.foreach(prop => {
			if (!properties.containsKey(prop)) {
				throw new IOException("Property file missing required property " + prop)
			}
		})
		properties
	}

	def contains(p: String): Boolean = properties.containsKey(p)

	def getOptionalString(p: String): Option[String] = {
		try {
			Option(properties.getProperty(p))
		} catch {
			case _ => None
		}
	}

	def getString(p: String): String = getOptionalString(p) match {
		case Some(v) => v
		case None => throw new Exception("Undefined required prop " + p)
	}

	def getBoolean(p: String): Boolean = getString(p) == "true"
}

