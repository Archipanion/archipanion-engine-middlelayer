import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.archipanion.mw.server.model.config.query.dynamicDescription.*
import org.archipanion.mw.sevice.model.context.QueryContext
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.operator.*
import org.vitrivr.engine.query.model.api.InformationNeedDescription


class QueryCreator() {
    /**
     * Takes a list of input data and a DynamicInformationNeedDescription and creates an InformationNeedDescription
     * @param queryInputs List of api inputs
     * @param dynInd DynamicInformationNeedDescription
     * @return InformationNeedDescription
     */
    val logger: KLogger = KotlinLogging.logger {}
    private val specificToDynamicMapper: MutableMap<String, Pair<Int, String>> = mutableMapOf()


    /**
    fun createQueryPipeline(): InformationNeedDescription {


    val (pipelineInputs, operations, output) = buildTreeFromOutput(dynInd)
    val context: QueryContext = QueryContext(global = mapOf("limit" to "30"))
    return InformationNeedDescription(pipelineInputs, operations, output, context)
    }


    fun buildTreeFromOutput(dynInd: DynamicInformationNeedDescription): Triple<MutableMap<String, InputData>, MutableMap<String, OperatorDescription>, String> {
    val piplineOutput: String = parseOutput(dynInd)
    val pipelineInputs: MutableMap<String, InputData> = mutableMapOf()
    val pipelineOperators: MutableMap<String, OperatorDescription> = mutableMapOf()
    var nodeName = piplineOutput
    var idx = 0
    do {
    val predecessor = findPredecessor(nodeName)

    }
    }


    fun findPredecessor(nodeInput: String): Pair<String, Any> {
    val predecessor = findPredecessorOperation(nodeInput)
    ?: findPredecessorInput(nodeInput)
    ?: throw DynamicPipelineConfigurationException("No predecessor found for $nodeInput")
    return predecessor
    }

    fun findPredecessorOperation(
    nodeInput: String
    ): Pair<String, DynamicOperation>? {
    val predecessor = this.dynInd.operations[nodeInput] ?: return null
    return Pair(nodeInput, predecessor)
    }

    fun findPredecessorInput(
    nodeInput: String
    ): Pair<String, DynamicInput>? {
    val predecessor = this.dynInd.inputs[nodeInput] ?: return null
    return Pair(nodeInput, predecessor)
    }

    fun parseOutput(dynInd: DynamicInformationNeedDescription): String {
    val output = dynInd.output ?: throw DynamicPipelineConfigurationException("No output found")
    if (output.contains("%i")) throw DynamicPipelineConfigurationException("Output must not contain %i")
    return output
    }
     */

    fun createQuery(
        queryInputs: Map<String, InputData>,
        dynInd: DynamicInformationNeedDescription
    ): InformationNeedDescription {
        this.specificToDynamicMapper.clear()
        val pipelineInputs: MutableMap<String, InputData> =
            createPipelineInputs(queryInputs, dynInd)
        val operations: MutableMap<String, OperatorDescription> =
            createOperations(pipelineInputs, dynInd)
        val output: String = dynInd.output
        val context: QueryContext = createContext(operations, dynInd)

        return InformationNeedDescription(pipelineInputs, operations,context, output )
    }

    fun createContext(
        operations: MutableMap<String, OperatorDescription>,
        dynInd: DynamicInformationNeedDescription,
    ): QueryContext
    {
        val local:  MutableMap<String, MutableMap<String, String>> = mutableMapOf()
        for ((operationName, operationDescription) in operations) {

            operationName.replace("[0-9]-[0-9]-".toRegex(),"%i-").let {
                key ->
                    if (dynInd.context.local.containsKey(key)) {
                        local[operationName] = mutableMapOf()
                        for ((property, value) in dynInd.context.local[key]!!) {
                            local[operationName]?.set(property, value)
                        }
                    }
            }

        }
        dynInd.context.local.forEach() {
           if (!it.key.contains("%i")){
               local[it.key] = mutableMapOf()
               for ((property, value) in it.value) {
                   local[it.key]?.set(property, value)
               }
           }
        }
        val qc = QueryContext(global = dynInd.context.global, local = local)
        return qc
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
            var idy = 1
            do {
                var foundSuccessor = false
                nextOperation@ for ((dynOperationName, dynOperationDescription) in dynInd.operations) {
                    if (dynOperationDescription.input.contains(dynParentName)) {
                        foundSuccessor = true
                        dynParentName = dynOperationName
                        var specificOperationName: String
                        if (dynOperationName.contains("%i")) {
                            specificOperationName = dynOperationName.replace("%i", "$idx-$idy")
                        } else {
                            specificOperationName = dynOperationName
                        }
                        if (operations.containsKey(specificOperationName)) {
                            when (operations[specificOperationName]!!.type) {
                                OperatorType.AGGREGATOR ->
                                    (operations[specificOperationName] as AggregatorDescription).inputs.add(
                                        dynOperationDescription.input.replace("%i", "$idx-${idy - 1}")
                                    )

                                else -> {

                                }
                            }
                            // Append index to end
                            val tmp = operations[specificOperationName]!!
                            operations.remove(specificOperationName)
                            operations[specificOperationName] = tmp
                        } else {
                            operations[specificOperationName] = operatorCreator(dynOperationDescription, idx, idy - 1)
                        }
                        idy++
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
        queryInputs: Map<String, InputData>,
        dynInd: DynamicInformationNeedDescription,
    ): MutableMap<String, InputData> {
        val pipelineInputs: MutableMap<String, InputData> = mutableMapOf()
        var idx = 0

        queryInputs@ for (input in queryInputs) {
            // Find matching input type
            var foundMatchingInput = false
            dynamicInputs@ for ((dynInputName, dynInputType) in dynInd.inputs) {
                // TODO: some convention to assigne more than one input of each type
                if (input.value.type.toString() == dynInputType.type.toString()) {
                    foundMatchingInput = true
                    // Check if input has an operation successor
                    var foundOperationSuccessor = false
                    checkOperations@ for ((dynOperationName, dynOperationDescription) in dynInd.operations) {
                        if (dynOperationDescription.input == dynInputName) {
                            foundOperationSuccessor = true

                            val specificInputName = dynInputName.replace("%i", "$idx-0")
                            this.specificToDynamicMapper[specificInputName] = Pair(idx, dynInputName)

                            pipelineInputs[specificInputName] = input.value
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
            idx++
        }
        return pipelineInputs
    }


    fun operatorCreator(dynOp: DynamicOperation, idx: Int, idy: Int): OperatorDescription {
        val input = dynOp.input.replace("%i", "$idx-$idy")
        when (dynOp.type) {
            OperationType.RETRIEVER -> {
                return RetrieverDescription(input, dynOp.field!!)
            }

            OperationType.TRANSFORMER -> {
                return TransformerDescription(dynOp.name!!, input)
            }

            OperationType.AGGREGATOR -> {
                val inputs = mutableListOf<String>()
                inputs.add(input)
                return AggregatorDescription(dynOp.name!!, inputs)
            }
        }
    }

}


