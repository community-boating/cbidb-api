package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.API.ResultError

case class CreateSignoutError(override val code: String, override val message: String, extras: List[CreateSignoutSingleError]) extends ResultError(code, message)

case class CreateSignoutSingleError(override val code: String, override val message: String, overridable: Boolean) extends ResultError(code, message)
