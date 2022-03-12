package cpa.ibgwapi.account

import cpa.ibgwapi.RequestError
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/account")
class AccountController(
    private val service: AccountService
) {
    @GetMapping("/summary")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK"),
        ApiResponse(responseCode = "502", description = "Could not connect to gateway", content = [Content(schema = Schema(implementation = RequestError::class))]),
        ApiResponse(responseCode = "504", description = "Gateway timeout", content = [Content(schema = Schema(implementation = RequestError::class))])
    )
    fun getAccountSummary(): AccountSummary =
        service.getAccountSummary()

    @GetMapping("/positions")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK"),
        ApiResponse(responseCode = "502", description = "Could not connect to gateway", content = [Content(schema = Schema(implementation = RequestError::class))]),
        ApiResponse(responseCode = "504", description = "Gateway timeout", content = [Content(schema = Schema(implementation = RequestError::class))])
    )
    fun getPositions(): List<Position> =
        service.getPositions()
}
