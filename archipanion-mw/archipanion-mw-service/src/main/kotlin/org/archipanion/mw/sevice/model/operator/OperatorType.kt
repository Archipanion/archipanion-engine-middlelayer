package org.archipanion.mw.sevice.model.operator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OperatorType {
    @SerialName("RETRIEVER")
    RETRIEVER,
    @SerialName("TRANSFORMER")
    TRANSFORMER,
    @SerialName("AGGREGATOR")
    AGGREGATOR
}