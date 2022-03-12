package cpa.ibgwapi.order

import com.ib.client.Contract
import com.ib.client.Decimal
import com.ib.client.Types
import cpa.ibgwapi.brokerage.IBOrder
import cpa.ibgwapi.brokerage.IBSession
import cpa.ibgwapi.brokerage.IBSessionContext
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val session: IBSession
) {
    private val logger = KotlinLogging.logger {}

    fun getAllOpenOrders(): List<Order> = session.run { ctx ->
        ctx.client.reqOpenOrders()
        val ibOrders = ctx.dataStore.orders
        val orders = synchronized(ctx.dataStore.lock) {
            ibOrders.map { ibOrder ->
                createOrderFromIbOrder(ibOrder)
            }
        }

        // Add all root orders to a map
        val map = orders
            .filter { it.parentId == null }
            .associateBy { it.id }

        // Associate all child order with parent orders
        orders
            .filter { it.parentId != null }
            .forEach { order ->
                map[order.parentId]?.childOrders?.add(order)
                    ?: logger.warn { "Could not find parent order with id ${order.parentId}" }
            }

        map.values.toList()
    }

    fun cancelOrder(orderId: Int) = session.run { ctx ->
        ctx.client.cancelOrder(orderId)
    }

    fun placeOrders(orders: List<Order>) = session.run { ctx ->
        synchronized(ctx.dataStore.lock) {
            ctx.dataStore.clearOrders()
        }
        var nextValidOrderId = ctx.client.reqNextValidOrderId()
        orders.forEach { order ->
            val orderId = nextValidOrderId
            placeOrder(ctx, order, orderId,0)
            order.childOrders.forEach { childOrder ->
                val childOrderId = ++nextValidOrderId
                placeOrder(ctx, childOrder, childOrderId, orderId)
            }
            nextValidOrderId++
        }
        // Allow some time for confirmation
        Thread.sleep(1500L)
    }

    private fun placeOrder(ctx: IBSessionContext, order: Order, orderId: Int, parentId: Int = 0) {
        val contract = Contract().apply {
            symbol(order.symbol)
            exchange("SMART")
            primaryExch("NYSE")
            secType("STK") // We only support equities
            currency("USD")
        }
        val ibOrder = com.ib.client.Order().apply {
            val orderType: com.ib.client.OrderType = when (order.type) {
                OrderType.LIMIT -> com.ib.client.OrderType.LMT
                OrderType.MARKET -> com.ib.client.OrderType.MKT
                OrderType.MARKET_ON_CLOSE -> com.ib.client.OrderType.MOC
                OrderType.STOP -> com.ib.client.OrderType.STP
                OrderType.STOP_LIMIT -> com.ib.client.OrderType.STP_LMT
            }
            if (order.goodTillDate != null) {
                goodTillDate(order.goodTillDate)
            }
            orderType(orderType)
            outsideRth(false)
            totalQuantity(Decimal.get(order.qty))
            val action: Types.Action = when (order.side) {
                OrderSide.BUY -> Types.Action.BUY
                OrderSide.SELL -> Types.Action.SELL
            }
            action(action)
            val tif: Types.TimeInForce = when (order.tif) {
                TimeInForce.DAY -> Types.TimeInForce.DAY
                TimeInForce.GTC -> Types.TimeInForce.GTC
                TimeInForce.GTD -> Types.TimeInForce.GTD
            }
            tif(tif)
            if (order.limitPrice != null) {
                lmtPrice(order.limitPrice)
            }
            if (order.stopPrice != null) {
                adjustedStopPrice(order.stopPrice)
            }
            if (parentId > 0) {
                parentId(parentId)
            }
        }
        ctx.client.placeOrder(orderId, contract, ibOrder)
    }

    private fun createOrderFromIbOrder(ibOrder: IBOrder): Order {
        var limitPrice: Double? = null
        var stopPrice: Double? = null

        val orderType: OrderType = when (ibOrder.order.orderType()) {
            com.ib.client.OrderType.LMT -> OrderType.LIMIT
            com.ib.client.OrderType.MKT -> OrderType.MARKET
            com.ib.client.OrderType.MOC -> OrderType.MARKET_ON_CLOSE
            com.ib.client.OrderType.STP -> OrderType.STOP
            com.ib.client.OrderType.STP_LMT -> OrderType.STOP_LIMIT
            else -> throw Exception("Invalid order type")
        }

        val status: OrderStatus = when (ibOrder.status) {
            com.ib.client.OrderStatus.Unknown -> OrderStatus.UNKNOWN
            com.ib.client.OrderStatus.ApiCancelled -> OrderStatus.API_CANCELLED
            com.ib.client.OrderStatus.ApiPending -> OrderStatus.API_PENDING
            com.ib.client.OrderStatus.Cancelled -> OrderStatus.CANCELLED
            com.ib.client.OrderStatus.Filled -> OrderStatus.FILLED
            com.ib.client.OrderStatus.Inactive -> OrderStatus.INACTIVE
            com.ib.client.OrderStatus.PendingCancel -> OrderStatus.PENDING_CANCEL
            com.ib.client.OrderStatus.PendingSubmit -> OrderStatus.PENDING_SUBMIT
            com.ib.client.OrderStatus.PreSubmitted -> OrderStatus.PRE_SUBMITTED
            com.ib.client.OrderStatus.Submitted -> OrderStatus.SUBMITTED
        }

        val orderSide: OrderSide = when (ibOrder.order.action()) {
            Types.Action.BUY -> OrderSide.BUY
            Types.Action.SELL -> OrderSide.SELL
            else -> throw Exception("Invalid order side")
        }

        if (orderType == OrderType.LIMIT) {
            limitPrice = ibOrder.order.lmtPrice()
        }
        if (orderType == OrderType.STOP) {
            stopPrice = ibOrder.order.adjustedStopPrice()
        }

        val tif: TimeInForce = when (ibOrder.order.tif()) {
            Types.TimeInForce.DAY -> TimeInForce.DAY
            Types.TimeInForce.GTC -> TimeInForce.GTC
            Types.TimeInForce.GTD -> TimeInForce.GTD
            else -> throw Exception("TimeInForce ${ibOrder.order.tif()} not supported")
        }

        return Order(
            id = ibOrder.orderId,
            parentId = if (ibOrder.order.parentId() > 0) ibOrder.order.parentId() else null,
            symbol = ibOrder.contract.symbol(),
            qty = ibOrder.order.totalQuantity().value().toDouble(),
            side = orderSide,
            type = orderType,
            tif = tif,
            limitPrice = limitPrice,
            stopPrice = stopPrice,
            status = status,
            goodTillDate = ibOrder.order.goodTillDate()
        )
    }
}
