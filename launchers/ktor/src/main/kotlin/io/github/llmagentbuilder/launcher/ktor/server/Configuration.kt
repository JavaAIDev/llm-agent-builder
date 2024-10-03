package io.github.llmagentbuilder.launcher.ktor.server

// Use this file to hold package-level internal functions that return receiver object passed to the `install` method.
import io.ktor.server.plugins.compression.*


/**
 * Application block for [Compression] configuration.
 *
 * This file may be excluded in .openapi-generator-ignore,
 * and application-specific configuration can be applied in this function.
 *
 * See http://ktor.io/features/compression.html
 */
internal fun ApplicationCompressionConfiguration(): CompressionConfig.() -> Unit {
    return {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
}
