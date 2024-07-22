package org.archipanion.mw.server.controller.api.rest


import QueryCreator
import com.fasterxml.jackson.core.JsonParseException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatus
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatusException
import org.archipanion.mw.server.model.config.RootConfig
import org.archipanion.mw.server.model.config.query.Queryset
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.util.config.ConfigReader
import org.archipanion.mw.sevice.controller.QueryService
import org.archipanion.mw.sevice.model.input.Inputs
import org.archipanion.mw.sevice.model.result.QueryResult
import org.archipanion.mw.sevice.util.serialization.KotlinxJsonMapper
import org.vitrivr.engine.query.model.api.InformationNeedDescription
import java.io.FileNotFoundException
import kotlin.time.measureTime


val logger: KLogger = KotlinLogging.logger {}

@OpenApi(
    path = "/api/v1/{schema}/{pipeline}/query",
    methods = [HttpMethod.POST],
    summary = "Finds segments for specified ids",
    operationId = "postExecuteQuery",
    tags = ["Retrieval"],
    requestBody = OpenApiRequestBody([OpenApiContent(Inputs::class)]),
    responses = [
        OpenApiResponse("200", [OpenApiContent(QueryResult::class)]),
        OpenApiResponse("400", [OpenApiContent(ErrorStatus::class)])
    ]
)
fun findSegments(ctx: Context) {
    logger.debug { "Query Segment with id" }
    val duration = measureTime {

        val schema = ctx.pathParam("schema")
        val pipeline = ctx.pathParam("pipeline")

        val config = ConfigReader().read<RootConfig>()

        logger.trace { "Config loaded: ${config.toString()}" }

        val querySet = config?.let { ConfigReader(it.queryConfigPath).read<Queryset>() } ?: throw ErrorStatusException(
            500,
            "Queryset not found"
        )
        val query = querySet.queries.find { it.name == pipeline } ?: throw ErrorStatusException(500, "Query not found")
        if (query.schemas.contains(schema).not()) throw ErrorStatusException(500, "Schema not found")
        val dind = try {
            ConfigReader(query.path).read<DynamicInformationNeedDescription>()
        } catch (e: FileNotFoundException) {
            throw ErrorStatusException(500, "Query not found")
        } catch (e: JsonParseException ) {
            throw ErrorStatusException(500, "Error parsing query file")
        } catch (e: Exception) {
            throw ErrorStatusException(500, "Query not found")
        }
        logger.trace { "Query pipeline $pipeline loaded: ${dind.toString()} for schema $schema" }

        //val inputs = mapOf<String, InputData>("1" to TextInputData("Tiger"), "1" to TextInputData("Lion"))
        // Todo: Error if two inputs have the same id
        val inputData = try {
            ctx.bodyAsClass<Inputs>()
        } catch (e: Exception) {
            throw ErrorStatusException(400, "Invalid request: ${e.message}")
        }


        val ind = QueryCreator().createQuery(inputData.inputs, dind!!)
        logger.trace { "Query loaded: ${ind.toString()}" }

        val payload = KotlinxJsonMapper.toJsonString(ind, InformationNeedDescription::class.java)
        val engineIp = config.engineEndpoints.find { it.schema == schema }?.ip ?: throw ErrorStatusException(500, "Engine Ip not found")
        val enginePort = config.engineEndpoints.find { it.schema == schema }?.port ?: throw ErrorStatusException(500, "Engine Port not found")
        val basePath = "http://$engineIp:$enginePort"
        logger.trace { "Sending query to engine $basePath" }
        val response = QueryService(basePath).postQuery(schema, payload)


        if (response != null) {
            ctx.json(response)
        }
    }
    logger.info { "Executing ${ctx.req().pathInfo} took $duration." }
}
