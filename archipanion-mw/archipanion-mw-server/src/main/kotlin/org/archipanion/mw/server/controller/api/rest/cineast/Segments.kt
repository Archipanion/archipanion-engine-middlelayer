package org.archipanion.mw.server.controller.api.rest.cineast


import QueryCreator
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import io.javalin.openapi.HttpMethod
import io.javalin.openapi.OpenApi
import io.javalin.openapi.OpenApiContent
import io.javalin.openapi.OpenApiResponse
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatus
import org.archipanion.mw.server.controller.api.rest.exceptions.ErrorStatusException
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.model.segment.SegmentQueryResult
import org.archipanion.mw.server.util.config.ConfigReader
import org.archipanion.mw.sevice.controller.QueryService
import org.archipanion.mw.sevice.model.input.Inputs
import org.archipanion.mw.sevice.model.result.QueryResult
import org.archipanion.mw.sevice.util.serialization.KotlinxJsonMapper
import org.vitrivr.engine.query.model.api.InformationNeedDescription
import kotlin.time.measureTime


val logger: KLogger = KotlinLogging.logger {}

@OpenApi(
    path = "/api/v1/{schema}/{pipeline}/query",
    methods = [HttpMethod.GET],
    summary = "Finds segments for specified ids",
    operationId = "postExecuteQuery",
    tags = ["Retrieval"],
    responses = [
        OpenApiResponse("200", [OpenApiContent(SegmentQueryResult::class)]),
        OpenApiResponse("400", [OpenApiContent(ErrorStatus::class)])
    ]
)
fun findSegments(ctx: Context) {
    logger.debug { "Query Segment with id" }
    val duration = measureTime {
        val dind = ConfigReader("./queryconfig/query-mlt.json").read<DynamicInformationNeedDescription>()
        logger.trace { "Query loaded: ${dind.toString()}" }

        //val inputs = listOf<InputData>(TextInputData("Tiger"), TextInputData("Lion"))

        val inputData = try {
            ctx.bodyAsClass<Inputs>()
        } catch (e: Exception) {
            throw ErrorStatusException(400, "Invalid request: ${e.message}")
        }


        val ind = QueryCreator().createQuery(inputData.inputs, dind!!)
        logger.trace { "Query loaded: ${ind.toString()}" }

        val payload = KotlinxJsonMapper.toJsonString(ind, InformationNeedDescription::class.java)
        val response = QueryService().postQuery("MVK", payload)


        if (response != null) {
            ctx.json(response)
        }
    }
    logger.info { "Executing ${ctx.req().pathInfo} took $duration." }
}