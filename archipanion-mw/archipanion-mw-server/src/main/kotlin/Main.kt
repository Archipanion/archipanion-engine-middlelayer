
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import io.javalin.openapi.CookieAuth
import io.javalin.openapi.plugin.OpenApiPlugin
import io.javalin.openapi.plugin.OpenApiPluginConfiguration
import io.javalin.openapi.plugin.SecurityComponentConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerPlugin
import io.javalin.plugin.bundled.CorsPluginConfig
import org.archipanion.mw.server.controller.api.cli.Cli
import org.archipanion.mw.server.controller.api.rest.configureApiRoutes
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatus
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatusException
import org.archipanion.mw.server.model.config.ApiConfig
import org.archipanion.mw.server.model.config.RootConfig
import org.archipanion.mw.server.model.config.query.Queryset
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.util.config.ConfigReader
import org.archipanion.mw.sevice.util.serialization.KotlinxJsonMapper


private val logger: KLogger = KotlinLogging.logger {}

/**
 * Entry point for archipanion engine middel layer
 */
fun main(args: Array<String>) {

    logger.debug { "Starting up archipanion middle layer with args ${args.toString()}" }

    val config = ConfigReader().read<RootConfig>()
    logger.trace { "Config loaded: ${config.toString()}" }
    val queryConfig = ConfigReader(config!!.queryConfigPath).read<Queryset>()
    logger.trace { "QueryConfig loaded: ${queryConfig.toString()}" }
    for (query in queryConfig!!.queries) {
        query.informationNeedDescription = ConfigReader(query.path).read<DynamicInformationNeedDescription>()
        logger.trace { "Query loaded: ${query.toString()}" }
    }

    val javalin = javelinSetup(config.apiEndpoint)


    /* Start the Javalin and CLI. */
    javalin.start(config.apiEndpoint.port)
    logger.info { "vitrivr engine API is listening on port ${config.apiEndpoint.port}." }
    val cli = cliSetup(config).start()

    /* End Javalin once Cli is stopped. */
    javalin.stop()
}

fun cliSetup(config: RootConfig) : Cli {
    val cli = Cli(config)
    return cli
}

fun webSocketSetup(config: RootConfig) {

}


fun javelinSetup(config: ApiConfig): Javalin {
    /* Prepare Javalin endpoint. */
    val javalin = Javalin.create { c ->
        c.jsonMapper(KotlinxJsonMapper)


        /* Registers Open API plugin. */
        c.plugins.register(
            OpenApiPlugin(
                OpenApiPluginConfiguration()
                    .withDocumentationPath("/swagger-docs")
                    .withDefinitionConfiguration { _, u ->
                        u.withOpenApiInfo { t ->
                            t.title = "vitrivr engine API"
                            t.version = "1.0.0"
                            t.description = "API for the vitrivr engine."
                        }
                        u.withSecurity(
                            SecurityComponentConfiguration().withSecurityScheme("CookieAuth", CookieAuth("SESSIONID"))
                        )
                    }
            )
        )
        c.http.maxRequestSize = 1024 * 1024 * 1024 /* 1GB */

        c.plugins.enableCors { u -> u.add(CorsPluginConfig::anyHost) }


        /* Registers Swagger Plugin. */
        c.plugins.register(
            SwaggerPlugin(
                SwaggerConfiguration().apply {
                    this.documentationPath = "/swagger-docs"
                    this.uiPath = "/swagger-ui"
                }
            )
        )
    }.routes {
        configureApiRoutes(config)
    }.exception(ErrorStatusException::class.java) { e, ctx ->
        ctx.status(e.statusCode).json(ErrorStatus(e.message))
    }.exception(Exception::class.java) { e, ctx ->
        ctx.status(500).json(ErrorStatus("Internal Server Error: '${e.message}' @ ${e.stackTrace.first()}"))
    }
    return javalin
}
