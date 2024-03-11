package org.archipanion.model.config.query

import kotlinx.serialization.Serializable

@Serializable
data class Queryset(
    val setname: String,
    val queries: List<Query> = emptyList()
)