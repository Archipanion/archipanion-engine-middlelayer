package org.archipanion.mw.server.model.config.query

import kotlinx.serialization.Serializable
import org.archipanion.mw.server.model.config.query.dynamicDescription.DynamicInformationNeedDescription


@Serializable
data class Query(
    val name: String,
    val description: String,
    val path: String,
    val schemas: List<String>,
    var informationNeedDescription: DynamicInformationNeedDescription? = null
)
