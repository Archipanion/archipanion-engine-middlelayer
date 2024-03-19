import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicOperations
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicPipelineConfigurationException
import org.archipanion.mw.server.model.config.query.dynamicDescription.OperationType
import org.archipanion.mw.sevice.model.context.QueryContext
import org.vitrivr.engine.query.model.api.InformationNeedDescription
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.operator.*


class QueryCreator() {
    /**
     * Takes a list of input data and a DynamicInformationNeedDescription and creates an InformationNeedDescription
     * @param queryInputs List of api inputs
     * @param dynInd DynamicInformationNeedDescription
     * @return InformationNeedDescription
     */
    val logger: KLogger = KotlinLogging.logger {}
    private val specificToDynamicMapper: MutableMap<String, Pair<Int, String>> = mutableMapOf()

    fun createQuery(
        queryInputs: List<InputData>,
        dynInd: DynamicInformationNeedDescription
    ): InformationNeedDescription {
        this.specificToDynamicMapper.clear()
        val pipelineInputs: MutableMap<String, InputData> =
            createPipelineInputs(queryInputs, dynInd)
        val operations: MutableMap<String, OperatorDescription> =
            createOperations(pipelineInputs, dynInd)
        val output: String = dynInd.output
        val context: QueryContext = QueryContext(global = mapOf("limit" to "30"))

        return InformationNeedDescription(pipelineInputs, operations, output, context)
    }


    fun createOperations(
        pipelineInputs: MutableMap<String, InputData>,
        dynInd: DynamicInformationNeedDescription,
    ): MutableMap<String, OperatorDescription> {
        val operations: MutableMap<String, OperatorDescription> = mutableMapOf()
        for ((inputName) in pipelineInputs) {

            val (idx, dynInputName) = this.specificToDynamicMapper[inputName]!!
            var pipelineReachedOutput = false
            var dynParentName = dynInputName
            do {
                var foundSuccessor = false
                nextOperation@ for ((dynOperationName, dynOperationDescription) in dynInd.operations) {
                    if (dynOperationDescription.input == dynParentName) {
                        foundSuccessor = true
                        dynParentName = dynOperationName
                        val specificOperationName = dynOperationName.replace("%i", idx.toString())
                        if (operations.containsKey(specificOperationName)) {
                            when (operations[specificOperationName]!!.type) {
                                OperatorType.AGGREGATOR ->
                                    (operations[specificOperationName] as AggregatorDescription).inputs.add(
                                        dynOperationDescription.input.replace("%i", idx.toString())
                                    )

                                else -> {

                                }
                            }
                        } else {
                            operations[specificOperationName] = operatorCreator(dynOperationDescription, idx)
                        }
                        break@nextOperation
                    }
                }
                if (!foundSuccessor) {
                    val m = "No successor found for operation $dynParentName"
                    logger.error { m }
                    throw DynamicPipelineConfigurationException(m)
                }
                if (dynParentName == dynInd.output) {
                    pipelineReachedOutput = true
                }
            } while (!pipelineReachedOutput)
        }
        return operations
    }


    /**
     * Takes a list of input data and a DynamicInformationNeedDescription and creates a map of pipeline inputs
     * The methode checks if each input from api has a corresponding input in the dynamic pipeline configuration
     * @param queryInputs List of api inputs
     * @param dind DynamicInformationNeedDescription
     * @return Map of pipeline inputs
     */
    fun createPipelineInputs(
        queryInputs: List<(InputData)>,
        dynInd: DynamicInformationNeedDescription,
    ): MutableMap<String, InputData> {
        val pipelineInputs: MutableMap<String, InputData> = mutableMapOf()
        var ic = 0

        queryInputs@ for (input in queryInputs) {
            // Find matching input type
            var foundMatchingInput = false
            dynamicInputs@ for ((dynInputName, dynInputType) in dynInd.inputs) {
                // TODO: some convention to assigne more than one input of each type
                if (input.type.toString() == dynInputType.type.toString()) {
                    foundMatchingInput = true
                    // Check if input has an operation successor
                    var foundOperationSuccessor = false
                    checkOperations@ for ((dynOperationName, dynOperationDescription) in dynInd.operations) {
                        if (dynOperationDescription.input == dynInputName) {
                            foundOperationSuccessor = true

                            val specificInputName = dynInputName.replace("%i", ic.toString())
                            this.specificToDynamicMapper[specificInputName] = Pair(ic, dynInputName)

                            pipelineInputs[specificInputName] = input
                            // Break on first successor found
                            break@checkOperations;
                        }
                    }
                    if (!foundOperationSuccessor) {
                        val m = "No operation found for input $dynInputName"
                        logger.error { m }
                        throw DynamicPipelineConfigurationException(m)
                    }
                    // Break on first matching input found
                    break@dynamicInputs
                }
            }
            if (!foundMatchingInput) throw DynamicPipelineConfigurationException("No matching input found")
            ic++
        }
        return pipelineInputs
    }


    fun operatorCreator(dynOp: DynamicOperations, idx: Int): OperatorDescription {
        val input = dynOp.input.replace("%i", idx.toString())
        when (dynOp.type) {
            OperationType.RETRIEVER -> {
                return RetrieverDescription(input, dynOp.properties["field"]!!)
            }

            OperationType.TRANSFORMER -> {
                return TransformerDescription(dynOp.name, input, dynOp.properties)
            }

            OperationType.AGGREGATOR -> {
                val inputs = mutableListOf<String>()
                inputs.add(input)
                return AggregatorDescription(dynOp.name, inputs, dynOp.properties)
            }
        }
    }

}


