package cpa.ibgwapi.thread

import com.google.common.util.concurrent.RateLimiter

@SuppressWarnings("UnstableApiUsage")
class RateLimiter {
    private val rateLimiter = RateLimiter.create(40.0)

    fun run(block: () -> Unit) {
        rateLimiter.acquire(1)
        block()
    }
}
