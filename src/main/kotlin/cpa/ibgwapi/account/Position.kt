package cpa.ibgwapi.account

data class Position(
    val symbol: String,
    val qty: Double,
    val avgCost: Double
)
