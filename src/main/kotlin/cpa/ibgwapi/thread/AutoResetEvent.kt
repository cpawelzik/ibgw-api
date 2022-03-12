package cpa.ibgwapi.thread

import java.util.concurrent.TimeoutException

class AutoResetEvent(open: Boolean) {
    private val monitor = Object()

    @Volatile
    private var isOpen = false

    init {
        isOpen = open
    }

    @Throws(InterruptedException::class)
    fun await() {
        synchronized(monitor) {
            while (!isOpen) {
                monitor.wait()
            }
            isOpen = false
        }
    }

    @Throws(InterruptedException::class, TimeoutException::class)
    fun await(timeout: Long) {
        synchronized(monitor) {
            val t = System.currentTimeMillis()
            while (!isOpen) {
                monitor.wait(timeout)
                if (System.currentTimeMillis() - t >= timeout) {
                    throw TimeoutException()
                }
            }
            isOpen = false
        }
    }

    fun set() {
        synchronized(monitor) {
            isOpen = true
            monitor.notifyAll()
        }
    }

    fun reset() {
        isOpen = false
    }
}
