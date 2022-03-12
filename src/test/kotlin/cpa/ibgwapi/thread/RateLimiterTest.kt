package cpa.ibgwapi.thread

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class RateLimiterTest {
    private val sut = RateLimiter()

    @Test
    fun `limits the rate for 100 calls`() {
        val elapsed = measureTimeMillis {
            repeat(100) {
                sut.run {
                }
            }
        }
        // 100 calls must take longer than 2 seconds
        // and less than 4 seconds
        assertTrue(elapsed > 2000L)
        assertTrue(elapsed < 4000L)
    }
}
