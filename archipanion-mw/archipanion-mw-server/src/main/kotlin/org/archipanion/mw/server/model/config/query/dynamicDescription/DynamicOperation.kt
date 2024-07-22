package org.archipanion.mw.server.model.config.query.dynamicDescription

import kotlinx.serialization.Serializable


@Serializable
data class DynamicOperation(
    val type: OperationType,
    val name: String? = null,
    val input: String,
    val field: String? = null,
)
