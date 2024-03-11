package org.archipanion.model.Config.Query.DynamicDescription

import kotlinx.serialization.Serializable


@Serializable
data class DynamicInput(
    val type: InputType,
)