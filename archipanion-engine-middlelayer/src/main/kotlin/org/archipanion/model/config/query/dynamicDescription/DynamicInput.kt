package org.archipanion.model.config.query.dynamicDescription

import kotlinx.serialization.Serializable


@Serializable
data class DynamicInput(
    val type: InputType,
)