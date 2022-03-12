package cpa.ibgwapi.brokerage

import com.ib.client.Contract
import com.ib.client.EClientSocket
import com.ib.client.EJavaSignal
import com.ib.client.EReader
import com.ib.client.Order
import cpa.ibgwapi.thread.AutoResetEvent
import cpa.ibgwapi.thread.RateLimiter
import mu.KotlinLogging
import java.lang.Exception
import java.util.concurrent.TimeoutException

/**
 * Client to send requests to IB Gateway.
 */
class IBClient(
    receiver: IBReceiver,
    val dataStore: IBDataStore,
    private val rateLimiter: RateLimiter
) : AutoCloseable {
    private val readerSignal = EJavaSignal()
    private val clientSocket = EClientSocket(receiver, readerSignal)
    private val logger = KotlinLogging.logger {}
    @Volatile
    private var requestCounter: Int = 0

    companion object {
        private const val TIMEOUT_MS = 15000L
    }

    fun connect(host: String, port: Int, clientId: Int) {
        clientSocket.eConnect(host, port, clientId)
        if (!clientSocket.isConnected) {
            throw Exception("Could not connect to IB gateway")
        }
        val reader = EReader(clientSocket, readerSignal).apply {
            start()
        }
        Thread { processMessages(reader) }.apply {
            start()
        }
    }

    fun reqAccountSummary() {
        val reqId = makeRequestId()
        val tags = listOf(
            "NetLiquidation", "TotalCashValue", "SettledCash", "AccruedCash",
            "BuyingPower", "EquityWithLoanValue", "InitMarginReq", "MaintMarginReq",
            "AvailableFunds", "DayTradesRemaining", "Leverage"
        )
        synchronized(dataStore.lock) {
            dataStore.clearAccountSummary()
        }
        rateLimiter.run {
            clientSocket.reqAccountSummary(reqId, "All", tags.joinToString(","))
        }
        waitForSignal(dataStore.accountSummaryEndSignal)
        rateLimiter.run {
            clientSocket.cancelAccountSummary(reqId)
        }
    }

    fun reqNextValidOrderId(): Int {
        synchronized(dataStore.lock) {
            dataStore.nextValidOrderId = 0
        }
        rateLimiter.run {
            clientSocket.reqIds(-1)
        }
        waitForSignal(dataStore.nextValidOrderIdCompleted)
        synchronized(dataStore.lock) {
            return dataStore.nextValidOrderId
        }
    }

    fun placeOrder(orderId: Int, contract: Contract, order: Order) = rateLimiter.run {
        clientSocket.placeOrder(orderId, contract, order)
    }

    fun cancelOrder(orderId: Int) = rateLimiter.run {
        clientSocket.cancelOrder(orderId)
        // Allow some time for confirmation
        Thread.sleep(1500L)
    }

    fun reqOpenOrders() {
        synchronized(dataStore.lock) {
            dataStore.clearOrders()
        }
        rateLimiter.run {
            clientSocket.reqOpenOrders()
        }
        waitForSignal(dataStore.orderRequestCompleted)
    }

    fun reqPositions() {
        synchronized(dataStore.lock) {
            dataStore.clearPositions()
        }
        rateLimiter.run {
            clientSocket.reqPositions()
        }
        rateLimiter.run {
            clientSocket.cancelPositions()
        }
        waitForSignal(dataStore.positionsRequestCompleted)
    }

    private fun waitForSignal(signal: AutoResetEvent) {
        try {
            signal.await(TIMEOUT_MS)
        } catch (e: TimeoutException) {
            throw IBTimeoutException("Timeout after waiting for signal for $TIMEOUT_MS milliseconds.")
        }
    }

    private fun makeRequestId(): Int =
        requestCounter++

    private fun processMessages(reader: EReader) {
        while (clientSocket.isConnected) {
            readerSignal.waitForSignal()
            try {
                reader.processMsgs()
            } catch (e: Exception) {
                logger.warn(e) { "Failed to process message from gateway" }
            }
        }
        logger.info { "Terminating message processing thread" }
    }

    override fun close() {
        if (clientSocket.isConnected) {
            logger.info { "Closing connection to gateway." }
            clientSocket.eDisconnect()
        }
    }
}
