package cpa.ibgwapi.brokerage

import cpa.ibgwapi.config.GatewayConfig
import cpa.ibgwapi.thread.RateLimiter
import mu.KotlinLogging
import org.springframework.cache.support.AbstractValueAdaptingCache
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
class IBSession(
    val gatewayConfig: GatewayConfig
) {
    private val lock = Any()
    private val logger = KotlinLogging.logger {}
    val rateLimiter = RateLimiter()

    /**
     * Creates a new Session.
     */
    fun <T> run(block: (IBSessionContext) -> T): T {
        synchronized(lock) {
            // Create DataStore
            val dataStore = IBDataStore()
            // Create IBReceiver
            val receiver = IBReceiver(dataStore)
            // Create Client
            val client = IBClient(receiver, dataStore, rateLimiter)
            try {
                try {
                    client.connect(gatewayConfig.host, gatewayConfig.port, gatewayConfig.clientID)
                } catch (e: Exception) {
                    throw IBConnectionFailedException("Could not connect to IB gateway. GatewayConfig=$gatewayConfig")
                }
                return block(IBSessionContext(client, dataStore))
            } catch (e: Exception) {
                logger.error(e) { "Error while running IB session." }
                throw e
            } finally {
                client.close()
            }
        }
    }
}
