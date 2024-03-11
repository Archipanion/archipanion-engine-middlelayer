package org.archipanion.mw.sevice.model.result

import kotlinx.serialization.Serializable
import org.archipanion.mw.sevice.model.retrievable.Retrieved
import org.archipanion.mw.sevice.model.retrievable.attributes.PropertyAttribute
import org.archipanion.mw.sevice.model.retrievable.attributes.ScoreAttribute

typealias RetrievableIdString = String

@Serializable
data class QueryResultRetrievable(val id: RetrievableIdString, val score: Float, val type: String, val parts: MutableList<RetrievableIdString>, val properties: Map<String, String>) {
    constructor(retrieved: Retrieved) : this(
        retrieved.id.toString(),
        retrieved.filteredAttribute<ScoreAttribute>()?.score ?: 0f,
        retrieved.type ?: "",
        mutableListOf(),
        retrieved.filteredAttributes<PropertyAttribute>().firstOrNull()?.properties ?: emptyMap()
    )
}
