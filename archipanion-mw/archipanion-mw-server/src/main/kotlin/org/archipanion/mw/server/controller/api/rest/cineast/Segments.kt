package org.archipanion.mw.server.controller.api.rest.cineast


import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.openapi.*
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.model.segment.SegmentQueryResult
import org.archipanion.mw.server.util.config.ConfigReader
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.input.TextInputData

import queryCreator


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
    val ind = queryCreator(inputs, dind!!)
    logger.trace { "Query loaded: ${ind.toString()}" }
}
