package ai.tech.core.misc.plugin.compression

import ai.tech.core.misc.plugin.compression.model.config.CompressionConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*


public fun Application.configureCompression(config: CompressionConfig?) {
    config?.takeIf { it.enable != false }?.let {
        // We can configure compression here
        install(Compression) {
            //GZIP
            it.gzip?.takeIf { it.enable != false }?.let {
                gzip {
                    it.priority?.let {
                        priority = it
                    }
                    it.minimumSize?.let {
                        minimumSize(512)
                    }
                    it.matchContentType?.let {
                        matchContentType(
                            *it.toTypedArray()
                        )
                    }
                    it.excludeContentType?.let {
                        excludeContentType(
                            *it.toTypedArray()
                        )
                    }
                }
            }

            // DEFLATE
            it.deflate?.takeIf { it.enable != false }?.let {
                deflate {
                    it.priority?.let {
                        priority = it
                    }
                    it.minimumSize?.let {
                        minimumSize(512)
                    }
                    it.matchContentType?.let {
                        matchContentType(
                            *it.toTypedArray()
                        )
                    }
                    it.excludeContentType?.let {
                        excludeContentType(
                            *it.toTypedArray()
                        )
                    }
                }
            }

            // IDENTITY
            it.identity?.takeIf { it.enable != false }?.let {
                identity {
                    // The minimum size of a response that will be compressed
                    it.priority?.let {
                        priority = it
                    }
                    it.minimumSize?.let {
                        minimumSize(512)
                    }
                    it.matchContentType?.let {
                        matchContentType(
                            *it.toTypedArray()
                        )
                    }
                    it.excludeContentType?.let {
                        excludeContentType(
                            *it.toTypedArray()
                        )
                    }
                }
            }
        }
    }
}