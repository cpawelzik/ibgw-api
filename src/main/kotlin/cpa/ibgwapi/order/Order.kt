package cpa.ibgwapi.order

import com.fasterxml.jackson.annotation.JsonInclude

enum class OrderSide {
    BUY,
    SELL
}

enum class OrderType {
    MARKET,
    MARKET_ON_CLOSE,
    LIMIT,
    STOP,
    STOP_LIMIT
}

enum class OrderStatus {
    API_PENDING,
    API_CANCELLED,
    PRE_SUBMITTED,
    PENDING_CANCEL,
    CANCELLED,
    SUBMITTED,
    FILLED,
    INACTIVE,
    PENDING_SUBMIT,
    UNKNOWN
}

enum class TimeInForce {
    DAY,
    GTC,
    GTD
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Order(
    // Only used when orders are requested
    val id: Int? = null,
    // Only used when orders are requested and order is a child order
    val parentId: Int? = null,
    val symbol: String,
    val qty: Double,
    val side: OrderSide,
    val type: OrderType,
    val tif: TimeInForce = TimeInForce.DAY,
    val limitPrice: Double?,
    val stopPrice: Double?,
    val goodTillDate: String?,
    // Only used when active orders are requested
    val status: OrderStatus = cpa.ibgwapi.order.OrderStatus.UNKNOWN,
    val childOrders: MutableList<Order> = mutableListOf()
)
