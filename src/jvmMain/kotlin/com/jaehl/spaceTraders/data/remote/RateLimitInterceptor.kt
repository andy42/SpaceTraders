package com.jaehl.spaceTraders.data.remote

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response

class RateLimitInterceptor (requestsPerSecond : Double) : Interceptor {

    private val rateLimiter : RateLimiter = RateLimiter.create(requestsPerSecond)

    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire()
        return chain.proceed(chain.request())
    }
}