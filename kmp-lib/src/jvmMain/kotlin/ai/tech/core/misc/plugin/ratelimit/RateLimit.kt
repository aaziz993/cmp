package ai.tech.core.misc.plugin.ratelimit

import ai.tech.core.misc.plugin.ratelimit.model.config.RateLimitsConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*

public fun Application.configureRateLimit(config: RateLimitsConfig?) =
    config?.takeIf { it.enable != false }?.let {
        install(RateLimit) {
            it.global?.let {
                global {
                    rateLimiter(it.limit, it.refillPeriod, it.initialSize)
                }
            }
            it.specific?.forEach {
                if (it.key.isBlank()) {
                    register {
                        rateLimiter(it.value.limit, it.value.refillPeriod, it.value.initialSize)
                    }
                }
                else {
                    register(RateLimitName(it.key)) {
                        rateLimiter(it.value.limit, it.value.refillPeriod, it.value.initialSize)
                    }
                }
            }
        }
    }
