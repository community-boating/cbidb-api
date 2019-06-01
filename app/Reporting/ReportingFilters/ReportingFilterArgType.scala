package Reporting.ReportingFilters

sealed abstract class ReportingFilterArgType

case object ARG_INT extends ReportingFilterArgType

case object ARG_DROPDOWN extends ReportingFilterArgType

case object ARG_DOUBLE extends ReportingFilterArgType

case object ARG_DATE extends ReportingFilterArgType

