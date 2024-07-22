package org.archipanion.mw.server.model.config.query.dynamicDescription;

import kotlinx.serialization.Serializable

@Serializable
class DynamicQueryContext(
    val local: Map<String, Map<String, String>> = emptyMap(),
    val global: Map<String, String> = emptyMap()
)