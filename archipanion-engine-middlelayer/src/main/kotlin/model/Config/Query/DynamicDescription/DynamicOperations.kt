package org.archipanion.model.Config.Query.DynamicDescription

import kotlinx.serialization.Serializable


@Serializable
data class DynamicOperations(
    val type: OperationType,
    val name: String,
    val input: String,
    val properties: Map<String, String> = emptyMap()
)
