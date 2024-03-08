package org.archipanion.Controller.Api.Rest.Exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ErrorStatus(val message: String)
