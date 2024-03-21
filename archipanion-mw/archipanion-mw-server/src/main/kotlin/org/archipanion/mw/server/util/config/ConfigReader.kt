package org.archipanion.mw.server.util.config

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

val logger: KLogger = KotlinLogging.logger {}


class ConfigReader(
    val DEFAULT_SCHEMA_PATH: String = "./config.json"
) {
        inline fun <reified T> read(): T? = read(Path.of(this.DEFAULT_SCHEMA_PATH))

        /**
         * Tries to read a [RootConfig] from a file specified by the given [Path].
         *
         * @param path The [Path] to read [RootConfig] from.
         * @return [RootConfig] or null, if an error occurred.
         */
        @OptIn(ExperimentalSerializationApi::class)
        inline fun <reified T> read(path: Path): T? = try {
            Files.newInputStream(path, StandardOpenOption.READ).use {
                Json.decodeFromStream<T>(it)
            }
        } catch (e: Throwable) {
            logger.error(e) { "Failed to read configuration from $path due to an exception." }
            throw e
        }
}
