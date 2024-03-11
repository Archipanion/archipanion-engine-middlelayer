


import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicOperations
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicPipelineConfigurationException
import org.archipanion.mw.server.model.config.query.dynamicDescription.OperationType
import org.archipanion.mw.sevice.model.context.QueryContext
import org.vitrivr.engine.query.model.api.InformationNeedDescription
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.operator.AggregatorDescription
import org.archipanion.mw.sevice.model.operator.OperatorDescription
import org.archipanion.mw.sevice.model.operator.RetrieverDescription
import org.archipanion.mw.sevice.model.operator.TransformerDescription


fun queryCreator(queryInputs: List<InputData>, dind: DynamicInformationNeedDescription): InformationNeedDescription {
    val pipelineInputs: MutableMap<String, InputData> = mutableMapOf()
    val operations: MutableMap<String, OperatorDescription> = mutableMapOf()
    var output: String = ""
    val context: QueryContext = QueryContext()

    val ic = 0
    for (input in queryInputs) {
        // find matching input type
        dind.inputs.forEach { (dynInputName, dynInputType) ->
            var dynParentName = dynInputName
            var dynChildName = ""

            // start on matching input type
            if (dynInputType.toString() == input.type.toString()) {

                val specificParentName = dynParentName.replace("%i", ic.toString())
                pipelineInputs[specificParentName] = input

                // find matchin input operation
                var found = false

                dind.operations.forEach { (dynOperationName, dynOperationDescription) ->
                    dynChildName = dynOperationName
                    if (dind.output == dynParentName) {
                        throw DynamicPipelineConfigurationException("Output operation cannot be an input operation")
                    }
                    if (dynOperationDescription.input == dynParentName) {
                        val specificChildName = dynChildName.replace("%i", ic.toString())
                        val operatorCreator = operatorCreator(dind.operations[dynChildName]!!)
                        operations[specificChildName] = operatorCreator as OperatorDescription
                        found = true
                        dynParentName = dynChildName
                    }
                }
                if (!found) {
                    throw DynamicPipelineConfigurationException("No operation found for input")
                }

                found = false
                output@ while(true) {
                    operations@ for ((dynOperationName, dynOperationDescription) in dind.operations) {
                        if (dind.output == dynParentName) {
                            output = dynOperationName
                            break@output
                        }

                        dynChildName = dynOperationName

                        if (dynOperationDescription.input == dynParentName) {
                            val specificChildName = dynChildName.replace("%i", ic.toString())
                            val operatorCreator = operatorCreator(dind.operations[dynChildName]!!)
                            operations[specificChildName] = operatorCreator as OperatorDescription
                            dynParentName = dynChildName
                            found = true
                            break@operations
                        }
                    }
                    if (!found) {
                        throw DynamicPipelineConfigurationException("No operation found for input")
                    }
                }
            }
        }

    }
    return InformationNeedDescription(pipelineInputs, operations, output, context)
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
            inputs.add(dyno.input)
            return AggregatorDescription(dyno.properties["aggregatorName"]!!, inputs, dyno.properties)
        }
    }
}


