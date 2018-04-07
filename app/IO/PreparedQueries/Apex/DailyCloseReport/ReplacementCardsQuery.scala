package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.ReplacementCardData
import Services.Authentication.ApexUserType

class ReplacementCardsQuery(closeId: Int) extends HardcodedQueryForSelect[ReplacementCardData](Set(ApexUserType)) {
  val getQuery: String =
    s"""
       |select
       |      p.name_first,
       |      p.name_last,
       |      pc.price * 100
       |      from persons p, persons_cards pc
       |      where pc.person_id = p.person_id
       |      and pc.close_id = $closeId
       |      and pc.price is not null
       |      order by 2,1
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): ReplacementCardData = new ReplacementCardData(
    fullName = rs.getString(2) + ", " + rs.getString(1),
    price = Currency.cents(rs.getInt(3))
  )
}