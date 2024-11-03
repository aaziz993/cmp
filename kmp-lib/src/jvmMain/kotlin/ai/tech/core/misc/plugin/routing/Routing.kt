package ai.tech.core.misc.plugin.routing

import ai.tech.core.data.model.Compression
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.plugin.cachingheaders.model.config.CacheControlConfig
import ai.tech.core.misc.plugin.routing.model.config.RoutingConfig
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.server.sessions.*
import java.io.File

public fun Application.configureRouting(config: RoutingConfig?, block: Routing.() -> Unit = {}) {
    config?.takeIf { it.enable != false }?.let {
        routing {
            it.staticRootPath?.let { staticRootFolder = File("static") }

            it.staticFiles?.takeIf { it.enable != false }?.let {
                staticFiles(it.remotePath, File(it.pathName), it.index) {
                    it.defaultPath?.let { default(it) }
                    it.enableAutoHeadResponse?.let {
                        if (it) {
                            enableAutoHeadResponse()
                        }
                    }
                    it.preCompressed?.let {
                        preCompressed(*it.map(::compressionToCompressedFileType).toTypedArray())
                    }
                    it.contentType?.let {
                        contentType { file ->
                            it[file.name]
                        }
                    }
                    it.cacheControl?.let {
                        cacheControl { file ->
                            (it[file.name]?.let {
                                CacheControlConfig
                                it.map {
                                    it.cacheControl()
                                }
                            } ?: emptyList())
                        }
                    }
                    it.extensions?.let {
                        exclude { file ->
                            it.fold(true) { acc, next -> acc && file.path.contains(next) }
                        }
                    }
                    it.extensions?.let { extensions(*it.toTypedArray()) }
                }
            }

            it.staticResources?.takeIf { it.enable != false }?.let {
                staticResources(it.remotePath, it.pathName, it.index) {
                    it.defaultPath?.let { default(it) }
                    it.preCompressed?.let { preCompressed(*it.map(::compressionToCompressedFileType).toTypedArray()) }
                    it.enableAutoHeadResponse?.let {
                        if (it) {
                            enableAutoHeadResponse()
                        }
                    }
                    it.contentType?.let {
                        contentType { url ->
                            it[url.file]
                        }
                    }
                    it.cacheControl?.let {
                        cacheControl { url ->
                            (it[url.file]?.let {
                                it.map {
                                    it.cacheControl()
                                }
                            } ?: emptyList())
                        }
                    }
                    it.extensions?.let {
                        exclude { url ->
                            it.fold(true) { acc, next -> acc && url.path.contains(next) }
                        }
                    }
                    it.extensions?.let { extensions(*it.toTypedArray()) }
                }
            }

            get("/logout") {
                call.sessions.clear<User>()
            }

            block()
        }
    }
}

private fun compressionToCompressedFileType(compression: Compression) = when (compression) {
    Compression.GZIP -> CompressedFileType.GZIP
    Compression.BROTLI -> CompressedFileType.BROTLI
    else -> throw IllegalArgumentException("Unknown compression type \"${compression.name}\"")
}