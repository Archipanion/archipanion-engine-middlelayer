package org.archipanion.model.config.query

import kotlinx.serialization.Serializable
import org.archipanion.Model.Config.Query.DynamicDescription.DynamicInformationNeedDescription

@Serializable
data class Query(
    val name: String,
    val description: String,
    val path: String,
    val schemas: List<String>,
    var informationNeedDescription: DynamicInformationNeedDescription? = null
)
