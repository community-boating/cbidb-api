package PDFBox.Reports.DailyCloseReport.Loader

import CbiUtil.Currency
import IO.PreparedQueries.Apex.DailyCloseReport._
import PDFBox.ReportLoader
import PDFBox.Reports.DailyCloseReport.Model.{DailyCloseReportModel, ParkingData}
import Services.PersistenceBroker

object DailyCloseReportLiveLoader extends ReportLoader[DailyCloseReportLiveParameter, DailyCloseReportModel] {
	override def apply(param: DailyCloseReportLiveParameter, pb: PersistenceBroker): DailyCloseReportModel = {
		val closeId = param.closeId
		val closeProps = pb.executePreparedQueryForSelect(new CloseProps(closeId)).head
		DailyCloseReportModel(
			closeId = closeId,
			closeProps = closeProps,
			inPersonCCTotal = {
				val result = pb.executePreparedQueryForSelect(new InPersonCCTotal(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.ccTotal
			},
			inPersonGCRedeemed = {
				val result = pb.executePreparedQueryForSelect(new InPersonGCRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			inPersonAPVouchersRedeemed = {
				val result = pb.executePreparedQueryForSelect(new InPersonAPVouchersRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			inPersonOtherARTotal = {
				val result = pb.executePreparedQueryForSelect(new InPersonOtherARTotal(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			taxRevenue = {
				val result = pb.executePreparedQueryForSelect(new TaxRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			sageRevenue = {
				val result = pb.executePreparedQueryForSelect(new SageRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			stripeRevenue = {
				val result = pb.executePreparedQueryForSelect(new StripeRevenue(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			onlineGCRedeemed = {
				val result = pb.executePreparedQueryForSelect(new OnlineGCRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			onlineAPVouchersRedeemed = {
				val result = pb.executePreparedQueryForSelect(new OnlineAPVouchersRedeemed(closeId))
				if (result.isEmpty) Currency.cents(0)
				else result.head.total
			},
			staff = pb.executePreparedQueryForSelect(new CloseStaff(closeId)),
			cash = pb.executePreparedQueryForSelect(new CloseCash(closeId)),
			ar = pb.executePreparedQueryForSelect(new ARQuery(closeId)),
			checks = pb.executePreparedQueryForSelect(new CloseChecks(closeId)),
			taxables = pb.executePreparedQueryForSelect(new TaxablesQuery(closeId)),
			membershipSales = pb.executePreparedQueryForSelect(new MembershipSalesQuery(closeId)),
			donations = pb.executePreparedQueryForSelect(new DonationsQuery(closeId)),
			jpSignups = pb.executePreparedQueryForSelect(new JpClassSignupsQuery(closeId)),
			apSignups = pb.executePreparedQueryForSelect(new ApClassSignupsQuery(closeId)),
			apVouchers = pb.executePreparedQueryForSelect(new ApVouchersQuery(closeId)),
			retail = pb.executePreparedQueryForSelect(new RetailQuery(closeId)),
			parking = {
				val list = pb.executePreparedQueryForSelect(new ParkingQuery(closeId))
				if (list.isEmpty) new ParkingData(0, 0, 0, 0, 0, Currency.cents(0))
				else if (list.length == 1) list.head
				else throw new Exception("Multiple parking data rows found for close " + closeId)
			},
			waiversAndPrivs = pb.executePreparedQueryForSelect(new WaiversPrivsQuery(closeId)),
			replacementCards = pb.executePreparedQueryForSelect(new ReplacementCardsQuery(closeId)),
			gcSales = pb.executePreparedQueryForSelect(new GCSalesQuery(closeId)),
			gcComps = pb.executePreparedQueryForSelect(new GCCompsQuery(closeId)),
			gcRedemptions = pb.executePreparedQueryForSelect(new GCRedemptionsQuery(closeId)),
			summaryItems = pb.executePreparedQueryForSelect(new SummaryQuery(closeId)),
		)
	}
}
