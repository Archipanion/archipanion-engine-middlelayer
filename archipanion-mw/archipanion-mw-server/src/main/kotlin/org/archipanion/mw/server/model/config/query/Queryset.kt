package org.archipanion.mw.server.model.config.query

import kotlinx.serialization.Serializable
import org.archipanion.mw.server.model.config.query.Query

@Serializable
data class Queryset(
    val setname: String,
    val queries: List<Query> = emptyList()
)