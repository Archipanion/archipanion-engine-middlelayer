package org.archipanion.mw.server.model.config

import kotlinx.serialization.Serializable

@Serializable
data class RootConfig (
    val schemas: List<SchemaConfig> = emptyList(),
    val apiEndpoint: ApiConfig = ApiConfig(),
    val engineEndpoints: List<ServiceConfig> = emptyList(),
    val queryConfigPath: String = "./queryconfig/queryset.json"
)