package org.archipanion.mw.server.model.config

import kotlinx.serialization.Serializable

@Serializable
data class SchemaConfig (
    val schema: String = "",
    val description: String = "",
)
