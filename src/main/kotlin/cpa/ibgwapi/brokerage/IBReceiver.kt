package cpa.ibgwapi.brokerage

import com.ib.client.Bar
import com.ib.client.CommissionReport
import com.ib.client.Contract
import com.ib.client.ContractDescription
import com.ib.client.ContractDetails
import com.ib.client.Decimal
import com.ib.client.DeltaNeutralContract
import com.ib.client.DepthMktDataDescription
import com.ib.client.EWrapper
import com.ib.client.Execution
import com.ib.client.FamilyCode
import com.ib.client.HistogramEntry
import com.ib.client.HistoricalSession
import com.ib.client.HistoricalTick
import com.ib.client.HistoricalTickBidAsk
import com.ib.client.HistoricalTickLast
import com.ib.client.NewsProvider
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.client.PriceIncrement
import com.ib.client.SoftDollarTier
import com.ib.client.TickAttrib
import com.ib.client.TickAttribBidAsk
import com.ib.client.TickAttribLast
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import java.lang.Exception
import kotlin.math.log

/**
 * EWrapper implementation that receives all callbacks from IBGW.
 */
class IBReceiver(
    private val dataStore: IBDataStore
) : EWrapper {

    private val logger = KotlinLogging.logger {}

    override fun tickPrice(tickerId: Int, field: Int, price: Double, attrib: TickAttrib?) {
        
    }

    override fun tickSize(tickerId: Int, field: Int, size: Decimal?) {
        
    }

    override fun tickOptionComputation(
        tickerId: Int,
        field: Int,
        tickAttrib: Int,
        impliedVol: Double,
        delta: Double,
        optPrice: Double,
        pvDividend: Double,
        gamma: Double,
        vega: Double,
        theta: Double,
        undPrice: Double
    ) {
        
    }

    override fun tickGeneric(tickerId: Int, tickType: Int, value: Double) {
        
    }

    override fun tickString(tickerId: Int, tickType: Int, value: String?) {
        
    }

    override fun tickEFP(
        tickerId: Int,
        tickType: Int,
        basisPoints: Double,
        formattedBasisPoints: String?,
        impliedFuture: Double,
        holdDays: Int,
        futureLastTradeDate: String?,
        dividendImpact: Double,
        dividendsToLastTradeDate: Double
    ) {
        
    }

    override fun orderStatus(
        orderId: Int,
        status: String?,
        filled: Decimal?,
        remaining: Decimal?,
        avgFillPrice: Double,
        permId: Int,
        parentId: Int,
        lastFillPrice: Double,
        clientId: Int,
        whyHeld: String?,
        mktCapPrice: Double
    ) {
        
    }

    override fun openOrder(orderId: Int, contract: Contract?, order: Order?, orderState: OrderState?) {
        logger.debug { "openOrder; orderId=$orderId, contract=$contract, order=$order; orderState=$orderState" }
        if (contract != null && order != null && orderState != null) {
            synchronized(dataStore.lock) {
                dataStore.orders.add(
                    IBOrder(
                        orderId,
                        contract,
                        order,
                        orderState.status()
                    )
                )
            }
        }
    }

    override fun openOrderEnd() {
        logger.debug { "openOrderEnd" }
        dataStore.orderRequestCompleted.set()
    }

    override fun updateAccountValue(key: String?, value: String?, currency: String?, accountName: String?) {
        
    }

    override fun updatePortfolio(
        contract: Contract?,
        position: Decimal?,
        marketPrice: Double,
        marketValue: Double,
        averageCost: Double,
        unrealizedPNL: Double,
        realizedPNL: Double,
        accountName: String?
    ) {
        
    }

    override fun updateAccountTime(timeStamp: String?) {
        
    }

    override fun accountDownloadEnd(accountName: String?) {
        
    }

    override fun nextValidId(orderId: Int) {
        synchronized(dataStore.lock) {
            logger.debug { "nextValidId: $orderId" }
            dataStore.nextValidOrderId = orderId
            dataStore.nextValidOrderIdCompleted.set()
        }
    }

    override fun contractDetails(reqId: Int, contractDetails: ContractDetails?) {
        
    }

    override fun bondContractDetails(reqId: Int, contractDetails: ContractDetails?) {
        
    }

    override fun contractDetailsEnd(reqId: Int) {
        
    }

    override fun execDetails(reqId: Int, contract: Contract?, execution: Execution?) {
        
    }

    override fun execDetailsEnd(reqId: Int) {
        
    }

    override fun updateMktDepth(
        tickerId: Int,
        position: Int,
        operation: Int,
        side: Int,
        price: Double,
        size: Decimal?
    ) {
        
    }

    override fun updateMktDepthL2(
        tickerId: Int,
        position: Int,
        marketMaker: String?,
        operation: Int,
        side: Int,
        price: Double,
        size: Decimal?,
        isSmartDepth: Boolean
    ) {
        
    }

    override fun updateNewsBulletin(msgId: Int, msgType: Int, message: String?, origExchange: String?) {
        
    }

    override fun managedAccounts(accountsList: String?) {
        
    }

    override fun receiveFA(faDataType: Int, xml: String?) {
        
    }

    override fun historicalData(reqId: Int, bar: Bar?) {
        
    }

    override fun scannerParameters(xml: String?) {
        
    }

    override fun scannerData(
        reqId: Int,
        rank: Int,
        contractDetails: ContractDetails?,
        distance: String?,
        benchmark: String?,
        projection: String?,
        legsStr: String?
    ) {
        
    }

    override fun scannerDataEnd(reqId: Int) {
        
    }

    override fun realtimeBar(
        reqId: Int,
        time: Long,
        open: Double,
        high: Double,
        low: Double,
        close: Double,
        volume: Decimal?,
        wap: Decimal?,
        count: Int
    ) {
        
    }

    override fun currentTime(time: Long) {
        
    }

    override fun fundamentalData(reqId: Int, data: String?) {
        
    }

    override fun deltaNeutralValidation(reqId: Int, deltaNeutralContract: DeltaNeutralContract?) {
        
    }

    override fun tickSnapshotEnd(reqId: Int) {
        
    }

    override fun marketDataType(reqId: Int, marketDataType: Int) {
        
    }

    override fun commissionReport(commissionReport: CommissionReport?) {
        
    }

    override fun position(account: String?, contract: Contract?, pos: Decimal?, avgCost: Double) {
        logger.debug { "position; Acc: $account; contract: $contract; pos:$pos, avgCost: $avgCost" }
        if (contract != null && pos != null) {
            synchronized(dataStore.lock) {
                dataStore.positions.add(
                    IBPosition(
                        contract = contract,
                        qty = pos.value().toDouble(),
                        avgCost = avgCost
                    )
                )
            }
        }
    }

    override fun positionEnd() {
        logger.debug { "positionEnd" }
        dataStore.positionsRequestCompleted.set()
    }

    override fun accountSummary(reqId: Int, account: String?, tag: String?, value: String?, currency: String?) {
        if (tag != null && value != null) {
            logger.debug { "accountSummary: $tag: $value" }
            val valueDouble = parseDouble(value)
            synchronized(dataStore.lock) {
                when (tag) {
                    "NetLiquidation" -> dataStore.netLiquidationValue = valueDouble
                    "TotalCashValue" -> dataStore.totalCashValue = valueDouble
                    "SettledCash" -> dataStore.settledCashValue = valueDouble
                    "AccruedCash" -> dataStore.accruedCash = valueDouble
                    "BuyingPower" -> dataStore.buyingPower = valueDouble
                    "EquityWithLoanValue" -> dataStore.equityWithLoanValue = valueDouble
                    "InitMarginReq" -> dataStore.initMarginReq = valueDouble
                    "MaintMarginReq" -> dataStore.maintMarginReq = valueDouble
                    "AvailableFunds" -> dataStore.availableFunds = valueDouble
                    "DayTradesRemaining" -> dataStore.dayTradesRemaining = valueDouble
                    "Leverage" -> dataStore.leverage = valueDouble
                }
            }
        }
    }

    private fun parseDouble(str: String): Double {
        return str.toDoubleOrNull() ?: 0.0
    }

    override fun accountSummaryEnd(reqId: Int) {
        logger.debug { "accountSummaryEnd" }
        // Raise signal
        dataStore.accountSummaryEndSignal.set()
    }

    override fun verifyMessageAPI(apiData: String?) {
        
    }

    override fun verifyCompleted(isSuccessful: Boolean, errorText: String?) {
        
    }

    override fun verifyAndAuthMessageAPI(apiData: String?, xyzChallenge: String?) {
        
    }

    override fun verifyAndAuthCompleted(isSuccessful: Boolean, errorText: String?) {
        
    }

    override fun displayGroupList(reqId: Int, groups: String?) {
        
    }

    override fun displayGroupUpdated(reqId: Int, contractInfo: String?) {
        
    }

    override fun error(e: Exception?) {
        e?.let{
            logger.error(it) { "IBReceiver.error" }
        }
    }

    override fun error(str: String?) {
        str?.let {
            logger.error { it }
        }
    }

    override fun error(id: Int, errorCode: Int, errorMsg: String?, advancedOrderRejectJson: String?) {
        // Note: errorCode can also be a warning
        logger.warn { "Error-Code/Warning: $errorCode" }
    }

    override fun connectionClosed() {
        logger.debug { "connectionClosed" }
    }

    override fun connectAck() {
        logger.debug { "connectAck" }
    }

    override fun positionMulti(
        reqId: Int,
        account: String?,
        modelCode: String?,
        contract: Contract?,
        pos: Decimal?,
        avgCost: Double
    ) {
        
    }

    override fun positionMultiEnd(reqId: Int) {
        
    }

    override fun accountUpdateMulti(
        reqId: Int,
        account: String?,
        modelCode: String?,
        key: String?,
        value: String?,
        currency: String?
    ) {
        
    }

    override fun accountUpdateMultiEnd(reqId: Int) {
        
    }

    override fun securityDefinitionOptionalParameter(
        reqId: Int,
        exchange: String?,
        underlyingConId: Int,
        tradingClass: String?,
        multiplier: String?,
        expirations: MutableSet<String>?,
        strikes: MutableSet<Double>?
    ) {
        
    }

    override fun securityDefinitionOptionalParameterEnd(reqId: Int) {
        
    }

    override fun softDollarTiers(reqId: Int, tiers: Array<out SoftDollarTier>?) {
        
    }

    override fun familyCodes(familyCodes: Array<out FamilyCode>?) {
        
    }

    override fun symbolSamples(reqId: Int, contractDescriptions: Array<out ContractDescription>?) {
        
    }

    override fun historicalDataEnd(reqId: Int, startDateStr: String?, endDateStr: String?) {
        
    }

    override fun mktDepthExchanges(depthMktDataDescriptions: Array<out DepthMktDataDescription>?) {
        
    }

    override fun tickNews(
        tickerId: Int,
        timeStamp: Long,
        providerCode: String?,
        articleId: String?,
        headline: String?,
        extraData: String?
    ) {
        
    }

    override fun smartComponents(reqId: Int, theMap: MutableMap<Int, MutableMap.MutableEntry<String, Char>>?) {
        
    }

    override fun tickReqParams(tickerId: Int, minTick: Double, bboExchange: String?, snapshotPermissions: Int) {
        
    }

    override fun newsProviders(newsProviders: Array<out NewsProvider>?) {
        
    }

    override fun newsArticle(requestId: Int, articleType: Int, articleText: String?) {
        
    }

    override fun historicalNews(
        requestId: Int,
        time: String?,
        providerCode: String?,
        articleId: String?,
        headline: String?
    ) {
        
    }

    override fun historicalNewsEnd(requestId: Int, hasMore: Boolean) {
        
    }

    override fun headTimestamp(reqId: Int, headTimestamp: String?) {
        
    }

    override fun histogramData(reqId: Int, items: MutableList<HistogramEntry>?) {
        
    }

    override fun historicalDataUpdate(reqId: Int, bar: Bar?) {
        
    }

    override fun rerouteMktDataReq(reqId: Int, conId: Int, exchange: String?) {
        
    }

    override fun rerouteMktDepthReq(reqId: Int, conId: Int, exchange: String?) {
        
    }

    override fun marketRule(marketRuleId: Int, priceIncrements: Array<out PriceIncrement>?) {
        
    }

    override fun pnl(reqId: Int, dailyPnL: Double, unrealizedPnL: Double, realizedPnL: Double) {
        
    }

    override fun pnlSingle(
        reqId: Int,
        pos: Decimal?,
        dailyPnL: Double,
        unrealizedPnL: Double,
        realizedPnL: Double,
        value: Double
    ) {
        
    }

    override fun historicalTicks(reqId: Int, ticks: MutableList<HistoricalTick>?, done: Boolean) {
        
    }

    override fun historicalTicksBidAsk(reqId: Int, ticks: MutableList<HistoricalTickBidAsk>?, done: Boolean) {
        
    }

    override fun historicalTicksLast(reqId: Int, ticks: MutableList<HistoricalTickLast>?, done: Boolean) {
        
    }

    override fun tickByTickAllLast(
        reqId: Int,
        tickType: Int,
        time: Long,
        price: Double,
        size: Decimal?,
        tickAttribLast: TickAttribLast?,
        exchange: String?,
        specialConditions: String?
    ) {
        
    }

    override fun tickByTickBidAsk(
        reqId: Int,
        time: Long,
        bidPrice: Double,
        askPrice: Double,
        bidSize: Decimal?,
        askSize: Decimal?,
        tickAttribBidAsk: TickAttribBidAsk?
    ) {
        
    }

    override fun tickByTickMidPoint(reqId: Int, time: Long, midPoint: Double) {
        
    }

    override fun orderBound(orderId: Long, apiClientId: Int, apiOrderId: Int) {
        
    }

    override fun completedOrder(contract: Contract?, order: Order?, orderState: OrderState?) {
        
    }

    override fun completedOrdersEnd() {
        
    }

    override fun replaceFAEnd(reqId: Int, text: String?) {
        
    }

    override fun wshMetaData(reqId: Int, dataJson: String?) {
        
    }

    override fun wshEventData(reqId: Int, dataJson: String?) {
        
    }

    override fun historicalSchedule(
        reqId: Int,
        startDateTime: String?,
        endDateTime: String?,
        timeZone: String?,
        sessions: MutableList<HistoricalSession>?
    ) {
        
    }

    override fun userInfo(reqId: Int, whiteBrandingId: String?) {
        
    }
}
