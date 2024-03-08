package org.archipanion.Model.Config.Query

import kotlinx.serialization.Serializable

@Serializable
data class Queryset(
    val setname: String,
    val queries: List<Query> = emptyList()
)