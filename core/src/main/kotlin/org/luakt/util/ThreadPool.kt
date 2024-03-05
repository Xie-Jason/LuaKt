package org.luakt.util

import java.util.concurrent.*

object ThreadPool {
    private var threadCount = 2
    private val pool : ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            threadCount - 1,
            threadCount - 1,
            30,
            TimeUnit.SECONDS,
            ArrayBlockingQueue(threadCount * 8),
            ThreadPoolExecutor.CallerRunsPolicy()
        )
    }

    fun init(threadCount : Int){
        ThreadPool.threadCount = threadCount
    }

    fun exec(task : ()->Unit) = pool.execute(task)

    fun <T> submit(task : Callable<T>) : Future<T> = pool.submit(task)

    fun destroy() {
        pool.shutdown()
    }
}