package org.sailcbi.APIServer.PDFBox

import org.sailcbi.APIServer.Services.PersistenceBroker

// A loader is a function from parameter to model
// a single report (e.g. close report) has a single class of model
// but potentially multiple loaders.  The live loader generates a model from a close id
// a test loader might return one of a premade set of test models from a case object test parameter, e.g.
abstract class ReportLoader[T <: ReportParameter, U <: ReportModel] {
	def apply(param: T, pb: PersistenceBroker[_]): U
}
