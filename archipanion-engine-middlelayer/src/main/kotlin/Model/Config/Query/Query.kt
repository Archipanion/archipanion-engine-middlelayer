package org.archipanion.Model.Config.Query

import kotlinx.serialization.Serializable
import org.vitrivr.engine.query.model.api.InformationNeedDescription

@Serializable
data class Query(
    val name: String,
    val description: String,
    val path: String,
    val schemas: List<String>,
    var informationNeedDescription: InformationNeedDescription? = null
)
