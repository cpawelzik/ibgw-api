package cpa.ibgwapi.account

import cpa.ibgwapi.brokerage.IBSession
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val session: IBSession
) {
    fun getAccountSummary(): AccountSummary = session.run { ctx ->
        ctx.client.reqAccountSummary()
        synchronized(ctx.dataStore.lock) {
            AccountSummary(
                netLiquidationValue = ctx.dataStore.netLiquidationValue,
                totalCashValue = ctx.dataStore.totalCashValue,
                settledCashValue = ctx.dataStore.settledCashValue,
                accruedCash = ctx.dataStore.accruedCash,
                buyingPower = ctx.dataStore.buyingPower,
                equityWithLoanValue = ctx.dataStore.equityWithLoanValue,
                initMarginReq = ctx.dataStore.initMarginReq,
                maintMarginReq = ctx.dataStore.maintMarginReq,
                availableFunds = ctx.dataStore.availableFunds,
                dayTradesRemaining = ctx.dataStore.dayTradesRemaining,
                leverage = ctx.dataStore.leverage
            )
        }
    }

    fun getPositions(): List<Position> = session.run { ctx ->
        ctx.client.reqPositions()
        synchronized(ctx.dataStore.lock) {
            ctx.dataStore.positions.map {
                Position(
                    symbol = it.contract.symbol(),
                    qty = it.qty,
                    avgCost = it.avgCost
                )
            }
        }
    }
}
