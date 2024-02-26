package org.archipanion.Model.Config

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig (
    val apiEndpoint: ApiConfig = ApiConfig()
)