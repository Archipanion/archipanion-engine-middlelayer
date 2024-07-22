package org.archipanion.mw.sevice.model.context

import kotlinx.serialization.Serializable

@Serializable
data class  QueryContext(
    /** properties applicable to all operators */
    val global: Map<String, String> = emptyMap(),

    /** properties per operator*/
    val local: Map<String, Map<String, String>> = emptyMap()
)
