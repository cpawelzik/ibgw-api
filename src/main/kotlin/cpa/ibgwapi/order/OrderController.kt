package cpa.ibgwapi.order

import cpa.ibgwapi.RequestError
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    val service: OrderService
) {
    @PostMapping
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK"),
        ApiResponse(responseCode = "502", description = "Could not connect to gateway", content = [Content(schema = Schema(implementation = RequestError::class))]),
        ApiResponse(responseCode = "504", description = "Gateway timeout", content = [Content(schema = Schema(implementation = RequestError::class))])
    )
    fun placeOrders(@RequestBody orders: List<Order>) =
        service.placeOrders(orders)

    @GetMapping
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK"),
        ApiResponse(responseCode = "502", description = "Could not connect to gateway", content = [Content(schema = Schema(implementation = RequestError::class))]),
        ApiResponse(responseCode = "504", description = "Gateway timeout", content = [Content(schema = Schema(implementation = RequestError::class))])
    )
    fun getActiveOrders(): List<Order> =
        service.getAllOpenOrders()

    @PostMapping("/cancel/{orderId}")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK"),
        ApiResponse(responseCode = "502", description = "Could not connect to gateway", content = [Content(schema = Schema(implementation = RequestError::class))]),
        ApiResponse(responseCode = "504", description = "Gateway timeout", content = [Content(schema = Schema(implementation = RequestError::class))])
    )
    fun cancelOrder(@PathVariable("orderId") orderId: Int) =
        service.cancelOrder(orderId)
}
