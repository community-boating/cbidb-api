package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Loader

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport._
import org.sailcbi.APIServer.PDFBox.ReportLoader
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.{DailyCloseReportModel, ParkingData}
import org.sailcbi.APIServer.Services.RequestCache

object DailyCloseReportLiveLoader extends ReportLoader[DailyCloseReportLiveParameter, DailyCloseReportModel] {
	override def apply(param: DailyCloseReportLiveParameter, rc: RequestCache): DailyCloseReportModel = {
		val closeId = param.closeId
		val closeProps = rc.executePreparedQueryForSelect(new CloseProps(closeId)).head
		DailyCloseReportModel(
			closeId = closeId,
			closeProps = closeProps,
			inPersonCCTotal = {
				val result = rc.executePreparedQueryForSelect(new InPersonCCTotal(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.ccTotal
			},
			inPersonGCRedeemed = {
				val result = rc.executePreparedQueryForSelect(new InPersonGCRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			inPersonAPVouchersRedeemed = {
				val result = rc.executePreparedQueryForSelect(new InPersonAPVouchersRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			inPersonOtherARTotal = {
				val result = rc.executePreparedQueryForSelect(new InPersonOtherARTotal(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			taxRevenue = {
				val result = rc.executePreparedQueryForSelect(new TaxRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			sageRevenue = {
				val result = rc.executePreparedQueryForSelect(new SageRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			stripeRevenue = {
				val result = rc.executePreparedQueryForSelect(new StripeRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			onlineGCRedeemed = {
				val result = rc.executePreparedQueryForSelect(new OnlineGCRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			onlineAPVouchersRedeemed = {
				val result = rc.executePreparedQueryForSelect(new OnlineAPVouchersRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			staff = rc.executePreparedQueryForSelect(new CloseStaff(closeId)),
			cash = rc.executePreparedQueryForSelect(new CloseCash(closeId)),
			ar = rc.executePreparedQueryForSelect(new ARQuery(closeId)),
			checks = rc.executePreparedQueryForSelect(new CloseChecks(closeId)),
			taxables = rc.executePreparedQueryForSelect(new TaxablesQuery(closeId)),
			membershipSales = rc.executePreparedQueryForSelect(new MembershipSalesQuery(closeId)),
			donations = rc.executePreparedQueryForSelect(new DonationsQuery(closeId)),
			jpSignups = rc.executePreparedQueryForSelect(new JpClassSignupsQuery(closeId)),
			apSignups = rc.executePreparedQueryForSelect(new ApClassSignupsQuery(closeId)),
			apVouchers = rc.executePreparedQueryForSelect(new ApVouchersQuery(closeId)),
			retail = rc.executePreparedQueryForSelect(new RetailQuery(closeId)),
			parking = {
				val list = rc.executePreparedQueryForSelect(new ParkingQuery(closeId))
				if (list.isEmpty) new ParkingData(0, 0, 0, 0, 0, Currency.cents(0))
				else if (list.length == 1) list.head
				else throw new Exception("Multiple parking data rows found for close " + closeId)
			},
			waiversAndPrivs = rc.executePreparedQueryForSelect(new WaiversPrivsQuery(closeId)),
			replacementCards = rc.executePreparedQueryForSelect(new ReplacementCardsQuery(closeId)),
			gcSales = rc.executePreparedQueryForSelect(new GCSalesQuery(closeId)),
			gcComps = rc.executePreparedQueryForSelect(new GCCompsQuery(closeId)),
			gcRedemptions = rc.executePreparedQueryForSelect(new GCRedemptionsQuery(closeId)),
			summaryItems = rc.executePreparedQueryForSelect(new SummaryQuery(closeId)),
		)
	}
}
