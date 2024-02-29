package org.archipanion.Model.Config

import kotlinx.serialization.Serializable

@Serializable
data class RootConfig (
    val schemas: List<SchemaConfig> = emptyList(),
    val apiEndpoint: ApiConfig = ApiConfig(),
    val engineEndpoints: List<ServiceConfig> = emptyList(),
)