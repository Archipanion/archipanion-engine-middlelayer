package org.archipanion.mw.server.controller.api.rest.cineast


import QueryCreator
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.openapi.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.model.segment.SegmentQueryResult
import org.archipanion.mw.server.util.config.ConfigReader
import org.archipanion.mw.server.util.serialization.KotlinxJsonMapper
import org.archipanion.mw.sevice.controller.QueryService
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.input.TextInputData
import org.vitrivr.engine.query.model.api.InformationNeedDescription


val logger: KLogger = KotlinLogging.logger {}

@OpenApi(
    path = "/api/v1/segments/{id}",
    methods = [HttpMethod.GET],
    summary = "Finds segments for specified ids",
    operationId = "postExecuteQuery",
    tags = ["Retrieval"],
    pathParams = [
        OpenApiParam("id", type = String::class, description = "The id of a specific segment.", required = true)
    ],
    responses = [
        OpenApiResponse("200", [OpenApiContent(SegmentQueryResult::class)]),
    ]
)
fun findSegments(ctx: Context) {
    logger.debug { "Query Segment with id: ${ctx.pathParam("id")}" }
    val dind = ConfigReader("./queryconfig/query-mlt.json").read<DynamicInformationNeedDescription>()
    logger.trace { "Query loaded: ${dind.toString()}" }
    val inputs = listOf<InputData>(TextInputData("Tiger") , TextInputData("Tiger"))
    val ind = QueryCreator().createQuery(inputs, dind!!)
    logger.trace { "Query loaded: ${ind.toString()}" }
    val payload = KotlinxJsonMapper.toJsonString(ind, InformationNeedDescription::class.java)
    val response = QueryService().postQuery("MVK", payload)
    if (response != null) {
        ctx.json(response)
    }
}
