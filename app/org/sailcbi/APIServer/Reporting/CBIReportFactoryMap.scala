package org.sailcbi.APIServer.Reporting

import com.coleji.neptune.Export.Report.ReportFactoryMap
import org.sailcbi.APIServer.Reporting.ReportFactories._

object CBIReportFactoryMap {
	val reportFactoryMap: ReportFactoryMap = Map(
		"ApClassInstance" -> ("AP Class Instance", classOf[ReportFactoryApClassInstance]),
		"JpClassInstance" -> ("JP Class Instance", classOf[ReportFactoryJpClassInstance]),
		"JpClassSignup" -> ("JP Class Signup", classOf[ReportFactoryJpClassSignup]),
		"Person" -> ("Person", classOf[ReportFactoryPerson]),
		"Donation" -> ("Donation", classOf[ReportFactoryDonation])
	)
}
