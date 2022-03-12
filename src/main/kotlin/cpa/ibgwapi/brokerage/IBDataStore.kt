package cpa.ibgwapi.brokerage

import com.ib.client.Contract
import com.ib.client.Order
import com.ib.client.OrderStatus
import cpa.ibgwapi.thread.AutoResetEvent

data class IBOrder(
    val orderId: Int,
    val contract: Contract,
    val order: Order,
    val status: OrderStatus = OrderStatus.Unknown
)

data class IBPosition(
    val contract: Contract,
    val qty: Double,
    val avgCost: Double
)

/**
 * Used as a shared data store between client and receiver.
 */
class IBDataStore {
    // Lock
    val lock = Any()
    // Account summary
    var accruedCash: Double = 0.0
    var buyingPower: Double = 0.0
    var equityWithLoanValue: Double = 0.0
    var initMarginReq: Double = 0.0
    var maintMarginReq: Double = 0.0
    var availableFunds: Double = 0.0
    var dayTradesRemaining: Double = 0.0
    var leverage: Double = 0.0
    var netLiquidationValue: Double = 0.0
    var totalCashValue: Double = 0.0
    var settledCashValue: Double = 0.0
    val accountSummaryEndSignal = makeSignal()
    // Orders
    val orders = mutableListOf<IBOrder>()
    var nextValidOrderId: Int = 0
    val nextValidOrderIdCompleted = makeSignal()
    val orderRequestCompleted = makeSignal()
    // Positions
    val positions = mutableListOf<IBPosition>()
    val positionsRequestCompleted = makeSignal()

    private fun makeSignal() =
        AutoResetEvent(false)

    fun clearOrders() {
        orders.clear()
        nextValidOrderId = 0
        nextValidOrderIdCompleted.reset()
        orderRequestCompleted.reset()
    }

    fun clearPositions() {
        positions.clear()
        positionsRequestCompleted.reset()
    }

    fun clearAccountSummary() {
        netLiquidationValue = 0.0
        accruedCash = 0.0
        equityWithLoanValue = 0.0
        initMarginReq = 0.0
        maintMarginReq = 0.0
        availableFunds = 0.0
        dayTradesRemaining = 0.0
        leverage = 0.0
        totalCashValue = 0.0
        settledCashValue = 0.0
        buyingPower = 0.0
        accountSummaryEndSignal.reset()
    }
}
