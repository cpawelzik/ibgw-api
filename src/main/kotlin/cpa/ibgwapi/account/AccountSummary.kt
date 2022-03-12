package cpa.ibgwapi.account

data class AccountSummary(
    val netLiquidationValue: Double,
    val accruedCash: Double,
    val buyingPower: Double,
    val equityWithLoanValue: Double,
    val initMarginReq: Double,
    val maintMarginReq: Double,
    val availableFunds: Double,
    val dayTradesRemaining: Double,
    val leverage: Double,
    val totalCashValue: Double,
    val settledCashValue: Double
)
