package PDFBox.Reports.JpClassRoster.Model

import PDFBox.ReportModel

case class JpClassRosterModel(
  rosterData: List[JpRosterData],
  instanceData: JpClassInstanceData
) extends ReportModel