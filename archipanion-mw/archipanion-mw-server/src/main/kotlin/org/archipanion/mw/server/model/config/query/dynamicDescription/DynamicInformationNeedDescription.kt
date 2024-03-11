package org.archipanion.mw.server.model.config.query.dynamicDescription

import kotlinx.serialization.Serializable

@Serializable
data class DynamicInformationNeedDescription(
    val name: String,
    val description: String,
    val inputs: Map<String, DynamicInput>,
    val operations: Map<String, DynamicOperations>,
    val output: String,
)