package org.archipanion.controller.api.rest.exceptions


data class ErrorStatusException(val statusCode: Int, override val message: String) : Exception(message) {

    val error: ErrorStatus
        get() = ErrorStatus(message)

}
