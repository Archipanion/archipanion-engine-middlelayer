package org.archipanion.mw.server.model.config.query.dynamicDescription

class SpecificToDynamicMapper(
    private val specificToDynamicMap: MutableMap<String, PipelineIndexMap> = mutableMapOf()
) {
    fun clear() {
        specificToDynamicMap.clear()
    }

    fun getDynName(inputName: String): String{
        return specificToDynamicMap[inputName]?.DynamicName ?: throw IllegalArgumentException("No dynamic name found for input $inputName")
    }

    fun getPrimaryIdx(inputName: String): Int {
        return specificToDynamicMap[inputName]?.idx ?: throw IllegalArgumentException("No dynamic name found for input $inputName")
    }

    fun getSecondaryIdy(inputName: String): Int {
        return specificToDynamicMap[inputName]?.idy ?: throw IllegalArgumentException("No dynamic name found for input $inputName")
    }

    fun addMapping(specificInputName: String, idx: Int,dynInputName: String) {
        addMapping(specificInputName, idx, 0, dynInputName)
    }
    fun addMapping(specificInputName: String, idx: Int, idy: Int, dynInputName: String) {
        this.specificToDynamicMap[specificInputName] = PipelineIndexMap(idx, idy, dynInputName)
    }

}