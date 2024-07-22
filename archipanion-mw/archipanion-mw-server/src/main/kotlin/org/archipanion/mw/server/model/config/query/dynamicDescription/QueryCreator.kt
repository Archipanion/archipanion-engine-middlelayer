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

    private var pipelineInputs: MutableMap<String, InputData>? = null
    private var operations: MutableMap<String, OperatorDescription>? = null
    private var output: String? = null
    private var context: QueryContext? = null
    private val specificToDynamicMapper: SpecificToDynamicMapper = SpecificToDynamicMapper()


    fun createQuery(
        queryInputs: Map<String, InputData>,
        dynInd: DynamicInformationNeedDescription
    ): InformationNeedDescription {
        this.specificToDynamicMapper.clear()
        this.pipelineInputs = createPipelineInputs(queryInputs, dynInd)
        this.operations = createOperations(dynInd)
        this.output = dynInd.output
        this.context = createContext(this.operations!!, dynInd)

        return InformationNeedDescription(this.pipelineInputs!!, this.operations!!, this.context!!, this.output!!)
    }

    fun createContext(
        operations: MutableMap<String, OperatorDescription>,
        dynInd: DynamicInformationNeedDescription,
    ): QueryContext {
        val local: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
        for ((operationName, operationDescription) in operations) {

            operationName.replace("[0-9]-[0-9]-".toRegex(), "%i-").let { key ->
                if (dynInd.context.local.containsKey(key)) {
                    local[operationName] = mutableMapOf()
                    for ((property, value) in dynInd.context.local[key]!!) {
                        local[operationName]?.set(property, value)
                    }
                }
            }

        }
        dynInd.context.local.forEach() {
            if (!it.key.contains("%i")) {
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
        dynInd: DynamicInformationNeedDescription,
    ): MutableMap<String, OperatorDescription> {
        val specificInputOperations = getInputOperations(dynInd)
        val operations: MutableMap<String, OperatorDescription> = mutableMapOf()
        specificInputOperations.forEach { (operationName, operationDescription) ->
            operationsDfs(operationName, operationDescription, dynInd, operations)
        }
        return (specificInputOperations+operations) as MutableMap<String, OperatorDescription>
    }

    private fun getInputOperations(dynInd: DynamicInformationNeedDescription): MutableMap<String, OperatorDescription> {
        val inputOperations = mutableMapOf<String, OperatorDescription>()
        this.pipelineInputs!!.forEach { (inputName, _) ->
            val inputOperators =
                dynInd.operations.filter { it.value.input == this.specificToDynamicMapper.getDynName(inputName) }
            val idx = this.specificToDynamicMapper.getPrimaryIdx(inputName) + 1
            var idy = 0
            inputOperators.forEach { (dynOperationName, inputOperatorDescription) ->
                dynOperationName.replace("%i", "$idx-$idy").let {
                    inputOperations[it] = operatorCreator(inputOperatorDescription, inputName)
                    this.specificToDynamicMapper.addMapping(it, idx, idy, dynOperationName)
                }
                idy += 1
            }
        }
        return inputOperations
    }


    private fun operationsDfs(
        operationName: String,
        inputOperators: OperatorDescription,
        dynInd: DynamicInformationNeedDescription,
        outOperations: MutableMap<String, OperatorDescription>
    ) {
        val successors =
            dynInd.operations.filter { it.value.input.split(",").contains(specificToDynamicMapper.getDynName(operationName)) }


        val idx = this.specificToDynamicMapper.getPrimaryIdx(operationName) + 1
        var idy = 0
        foreachSuccessor@ for ((dynOperationName, dyntOperatorDescription) in successors) {
            if (dynOperationName == this.output) {
                continue@foreachSuccessor
            }
            dynOperationName.replace("%i", "$idx-$idy").let { specificName ->
                operatorCreator(dyntOperatorDescription, operationName).let { createdOperator ->
                    duplicateAwareOperatorAdder(specificName, createdOperator, outOperations)
                    this.specificToDynamicMapper.addMapping(specificName, idx, idy, dynOperationName)
                    operationsDfs(specificName, createdOperator, dynInd, outOperations)
                }
            }
        }
        idy += 1
    }

    private fun duplicateAwareOperatorAdder(specificOperationName: String, createdOperator: OperatorDescription, outOperations: MutableMap<String, OperatorDescription>){
        if (outOperations.containsKey(specificOperationName)){
            when (outOperations[specificOperationName]!!.type) {
                OperatorType.AGGREGATOR ->
                    (outOperations[specificOperationName] as AggregatorDescription).inputs.addAll(
                        (createdOperator as AggregatorDescription).inputs
                    )
                else -> {

                }
            }
            // Append index to end
            val tmp = outOperations[specificOperationName]!!
            outOperations.remove(specificOperationName)
            outOperations[specificOperationName] = tmp
        } else {
            outOperations[specificOperationName] = createdOperator
        }
    }


    fun createOperations2(
        pipelineInputs: MutableMap<String, InputData>,
        dynInd: DynamicInformationNeedDescription,
    ): MutableMap<String, OperatorDescription> {
        val operations: MutableMap<String, OperatorDescription> = mutableMapOf()
        for ((inputName) in pipelineInputs) {

            val dynInputName = this.specificToDynamicMapper.getDynName(inputName)
            val idx = this.specificToDynamicMapper.getPrimaryIdx(inputName)
            val inputOperators = dynInd.operations.filter { it.value.input == dynInputName }
            for ((inputOperator) in inputOperators) {
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
                                operations[specificOperationName] =
                                    operatorCreator(dynOperationDescription, idx, idy - 1)
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
    private fun createPipelineInputs(
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
                            this.specificToDynamicMapper.addMapping(specificInputName, idx, dynInputName)

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

    private fun operatorCreator(dynOp: DynamicOperation, specificInputName: String): OperatorDescription {
        when (dynOp.type) {
            OperationType.RETRIEVER -> {
                return RetrieverDescription(specificInputName, dynOp.field!!)
            }

            OperationType.TRANSFORMER -> {
                return TransformerDescription(dynOp.name!!, specificInputName)
            }

            OperationType.AGGREGATOR -> {
                val inputs = mutableListOf<String>()
                inputs.add(specificInputName)
                return AggregatorDescription(dynOp.name!!, inputs)
            }
        }
    }

    private fun operatorCreator(dynOp: DynamicOperation, idx: Int, idy: Int): OperatorDescription {
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


