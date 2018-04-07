package PDFBox.Reports.JpSpecialNeedsReport.Loader

import IO.PreparedQueries.Apex.JpSpecialNeedsReport.GetSpecialNeedsData
import PDFBox.ReportLoader
import PDFBox.Reports.JpSpecialNeedsReport.Model.JpSpecialNeedsReportModel
import Services.PersistenceBroker

object JpSpecialNeedsReportLiveLoader extends ReportLoader[JpSpecialNeedsReportLiveParameter, JpSpecialNeedsReportModel]{
  override def apply(param: JpSpecialNeedsReportLiveParameter, pb: PersistenceBroker): JpSpecialNeedsReportModel = {
    val specNeedsData = pb.executePreparedQueryForSelect(new GetSpecialNeedsData(param.from, param.to))

    JpSpecialNeedsReportModel(
      specNeedsData,
      param.from,
      param.to
    )
  }
}
