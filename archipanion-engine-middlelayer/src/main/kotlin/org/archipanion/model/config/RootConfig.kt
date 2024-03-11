package org.archipanion.model.config

import kotlinx.serialization.Serializable
import org.archipanion.Model.Config.SchemaConfig

@Serializable
data class RootConfig (
    val schemas: List<SchemaConfig> = emptyList(),
    val apiEndpoint: ApiConfig = ApiConfig(),
    val engineEndpoints: List<ServiceConfig> = emptyList(),
    val queryConfigPath: String = "./queryconfig/queryset.json"
)