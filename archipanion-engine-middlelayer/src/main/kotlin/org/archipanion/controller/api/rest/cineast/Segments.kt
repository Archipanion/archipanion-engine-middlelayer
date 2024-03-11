package org.archipanion.controller.api.rest.cineast


import io.javalin.http.Context
import io.javalin.openapi.*
import org.archipanion.Model.Config.Query.DynamicDescription.DynamicInformationNeedDescription
import org.archipanion.Model.Config.Query.DynamicDescription.DynamicOperations
import org.archipanion.Model.Config.Query.DynamicDescription.OperationType
import org.archipanion.Model.Segment.SegmentQueryResult
import org.archipanion.Service.Engine.Model.OperatorDescription
import org.archipanion.Util.Config.ConfigReader
import org.archipanion.Util.Config.logger
import org.archipanion.model.config.query.dynamicDescription.DynamicInformationNeedDescription


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
    val ind = ConfigReader("./queryconfig/query-mlt.json").read<DynamicInformationNeedDescription>()
    logger.trace { "Query loaded: ${ind.toString()}" }
    val inputs = mutableListOf("Lion", "Tiger")

}
