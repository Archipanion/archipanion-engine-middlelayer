package org.archipanion.Controller.Api.Rest.Cineast


import io.javalin.http.Context
import io.javalin.http.TemporaryRedirectResponse
import io.javalin.openapi.*
import org.archipanion.Model.Config.Query.DynamicDescription.DynamicInformationNeedDescription
import org.archipanion.Model.Config.Query.DynamicDescription.DynamicOperations
import org.archipanion.Model.Config.Query.DynamicDescription.OperationType
import org.archipanion.Model.Segment.SegmentQueryResult
import org.archipanion.Util.Config.ConfigReader
import org.archipanion.Util.Config.logger
import org.openapitools.client.*
import org.vitrivr.engine.query.model.api.operator.AggregatorDescription
import org.vitrivr.engine.query.model.api.operator.OperatorDescription
import org.vitrivr.engine.query.model.api.operator.RetrieverDescription
import org.vitrivr.engine.query.model.api.operator.TransformerDescription

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

fun queryCreator(queryInputs: List<org.openapitools.client.models.InputData>, dind: DynamicInformationNeedDescription): org.openapitools.client.models.InformationNeedDescription {
    val pipelineInputs: MutableMap<String, org.openapitools.client.models.InputData> = mutableMapOf()
    val operations: MutableMap<String, org.openapitools.client.models.OperatorDescription > = mutableMapOf()
    val output: String = "output"
    val context: org.openapitools.client.models.QueryContext = org.openapitools.client.models.QueryContext()

    val ic = 0
    for (input in queryInputs) {
        // find matching input type
        dind.inputs.forEach { (dynInputName, dynInputType) ->
            if (dynInputType.toString() == input.type.toString()) {
                val specificInputName = dynInputName.replace("#", ic.toString())
                pipelineInputs[specificInputName] = input
                dind.operations.forEach { (dynOperationName, dynOperationDescription) ->
                    if (dynOperationDescription.input == dynInputName) {
                        val specificOperationName = dynOperationName.replace("#", ic.toString())
                        val operatorCreator = operatorCreator(dind.operations[dynOperationName]!!)
                        operations[specificOperationName] = operatorCreator as org.openapitools.client.models.OperatorDescription
                    }
                }
            }
        }

    }
    return org.openapitools.client.models.InformationNeedDescription(pipelineInputs, operations, output, context)
}

fun operatorCreator(dyno: DynamicOperations): OperatorDescription {
    when (dyno.type) {
        OperationType.RETRIEVER -> {
            return  RetrieverDescription(dyno.input, dyno.properties["field"]!!)
        }

        OperationType.TRANSFORMER -> {
            return TransformerDescription(dyno.properties["transformerName"]!!, dyno.input, dyno.properties)
        }

        OperationType.AGGREGATOR -> {
            val inputs = mutableListOf<String>()
            return AggregatorDescription(dyno.properties["aggregatorName"]!!, inputs, dyno.properties)
        }
    }
}


