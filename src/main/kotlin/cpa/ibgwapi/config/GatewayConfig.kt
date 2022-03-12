package cpa.ibgwapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class GatewayConfig(
    @Value("\${gateway.host}")
    val host: String,
    @Value("\${gateway.port}")
    val port: Int,
    @Value("\${gateway.clientId}")
    val clientID: Int
)
