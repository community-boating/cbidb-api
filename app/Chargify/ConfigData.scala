package Chargify

import CbiUtil.PropertiesWrapper

case class ConfigData(subdomain: String, apiKey: String)

object ConfigData {
  def getFromPropertiesWrapper(propsLocation: String) = {
    val pw = new PropertiesWrapper(propsLocation)
    ConfigData(pw.getProperty("subdomain"), pw.getProperty("apiKey"))
  }
}