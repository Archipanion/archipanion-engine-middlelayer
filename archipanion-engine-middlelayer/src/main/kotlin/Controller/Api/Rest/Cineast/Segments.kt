package org.archipanion.Controller.Api.Rest.Cineast

import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*
import org.archipanion.Model.Segment.SegmentQueryResult
import org.archipanion.Util.Config.logger

import kotlin.time.measureTime

@OpenApi(
    path = "/api/v1/segments/{id}",
    methods = [HttpMethod.POST],
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
}