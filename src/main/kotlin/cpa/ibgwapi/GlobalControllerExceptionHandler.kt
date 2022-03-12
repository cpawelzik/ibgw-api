package cpa.ibgwapi

import cpa.ibgwapi.brokerage.IBConnectionFailedException
import cpa.ibgwapi.brokerage.IBTimeoutException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class RequestError(
    val error: String
)

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(IBConnectionFailedException::class)
    fun handleClientConnectionFailed(e: Exception): ResponseEntity<RequestError> {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            RequestError(error = e.message ?: "Could not connect to IB gateway")
        )
    }

    @ExceptionHandler(IBTimeoutException::class)
    fun handleTimeout(e: Exception): ResponseEntity<RequestError> {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
            RequestError(error = e.message ?: "Timeout while waiting for IB gateway response")
        )
    }
}
