package org.archipanion.Controller.api.Rest.Exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ErrorStatus(val message: String)
