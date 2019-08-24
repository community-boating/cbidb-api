package org.sailcbi.APIServer.CbiUtil

abstract class DateComparison(val comparator: String)

case object DATE_= extends DateComparison("=")

case object DATE_!= extends DateComparison("!=")

case object DATE_<= extends DateComparison("<=")

case object DATE_< extends DateComparison("<")

case object DATE_>= extends DateComparison(">=")

case object DATE_> extends DateComparison(">")
