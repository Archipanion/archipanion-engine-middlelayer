package org.archipanion.mw.server.controller.api.rest.exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ErrorStatus(val message: String)
