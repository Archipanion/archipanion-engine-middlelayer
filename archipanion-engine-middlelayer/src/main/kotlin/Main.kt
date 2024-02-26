package org.archipanion

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.archipanion.Model.Config.ServerConfig
import org.archipanion.Util.Config.ConfigReader


private val logger: KLogger = KotlinLogging.logger {}

/**
 * Entry point for archipanion engine middel layer
 */
fun main(args: Array<String>) {
    logger.debug { "Starting up archipanion middle layer with args ${args.toString()}" }



    val config = ConfigReader().read<ServerConfig>()
    logger.trace { "Config loaded: ${config.toString()}" }


}