package org.sailcbi.APIServer.Storable

// table alias => whole sql string
case class Filter(makeSQLString: String => String)
