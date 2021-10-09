package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.ReportModel
import com.coleji.neptune.Util.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport._

case class DailyCloseReportModel(
	closeId: Int,
	closeProps: ClosePropsResult,
	inPersonCCTotal: Currency,
	inPersonGCRedeemed: Currency,
	inPersonAPVouchersRedeemed: Currency,
	inPersonOtherARTotal: Currency,
	taxRevenue: Currency,
	sageRevenue: Currency,
	stripeRevenue: Currency,
	onlineGCRedeemed: Currency,
	onlineAPVouchersRedeemed: Currency,
	staff: List[CloseStaffResult],
	cash: List[CloseCashResult],
	ar: List[AR],
	checks: List[Check],
	taxables: List[TaxablesItem],
	membershipSales: List[MembershipSale],
	donations: List[Donation],
	jpSignups: List[JPClassData],
	apSignups: List[APClassData],
	apVouchers: List[APVoucherData],
	retail: List[RetailData],
	parking: ParkingData,
	waiversAndPrivs: List[WaiverPrivData],
	replacementCards: List[ReplacementCardData],
	gcSales: List[GCSalesData],
	gcComps: List[GCCompsData],
	gcRedemptions: List[GCRedemptionData],
	summaryItems: List[SummaryItem]
) extends ReportModel
