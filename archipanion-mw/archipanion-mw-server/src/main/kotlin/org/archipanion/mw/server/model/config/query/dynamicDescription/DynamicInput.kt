package org.archipanion.mw.server.model.config.query.dynamicDescription

import kotlinx.serialization.Serializable


@Serializable
data class DynamicInput(
    val type: InputType,
)