package io.github.llmagentbuilder.launcher.ktor.server

import io.ktor.server.plugins.compression.*

internal fun applicationCompressionConfiguration(): CompressionConfig.() -> Unit {
    return {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }
}
