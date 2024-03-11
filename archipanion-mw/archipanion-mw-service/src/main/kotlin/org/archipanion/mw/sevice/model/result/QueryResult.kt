package org.archipanion.mw.sevice.model.result

import kotlinx.serialization.Serializable
import org.archipanion.mw.sevice.model.retrievable.Retrieved
import org.archipanion.mw.sevice.model.retrievable.attributes.RelationshipAttribute


@Serializable
data class QueryResult(val retrievables: List<QueryResultRetrievable>) {

    companion object {

        private fun fromRetrieved(retrieved: Collection<Retrieved>) : List<QueryResultRetrievable> {

            val results = retrieved.map { QueryResultRetrievable(it) }.associateBy { it.id }

            //map partOf relations the right way around
            retrieved.forEach { r: Retrieved ->
                val relationships = r.filteredAttribute<RelationshipAttribute>()?.relationships
                if (relationships != null) {
                    relationships.filter { it.pred == "partOf" && it.sub.first == r.id }.forEach {
                        results[it.obj.first.toString()]?.parts?.add(r.id.toString())
                    }
                    relationships.filter { it.pred == "partOf" && it.obj.first == r.id }.forEach {
                        results[r.id.toString()]?.parts?.add(it.sub.first.toString())
                    }
                }
            }

            return results.values.toList().sortedByDescending { it.score }

        }

    }

    constructor(retrieved: Collection<Retrieved>) : this(fromRetrieved(retrieved))
}
