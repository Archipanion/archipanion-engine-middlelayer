package org.archipanion.model.config

import kotlinx.serialization.Serializable

@Serializable
data class SchemaConfig (
    val schema: String = ""
)
