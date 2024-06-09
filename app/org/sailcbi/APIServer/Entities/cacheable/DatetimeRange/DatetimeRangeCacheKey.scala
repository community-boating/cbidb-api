package org.sailcbi.APIServer.Entities.cacheable.DatetimeRange

import java.time.LocalDate

case class DatetimeRangeCacheKey (startDate: LocalDate, endDate: LocalDate, rangeType: String)
