package org.archipanion.Model.Config.Query

import kotlinx.serialization.Serializable
import org.vitrivr.engine.query.model.api.InformationNeedDescription
import java.nio.file.Path

@Serializable
data class Queryset(
    val setname: String,
    val queries: List<Query> = emptyList()
)