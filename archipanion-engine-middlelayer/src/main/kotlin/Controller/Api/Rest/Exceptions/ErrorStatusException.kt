package org.archipanion.Controller.Api.Rest.Exceptions


data class ErrorStatusException(val statusCode: Int, override val message: String) : Exception(message) {

    val error: ErrorStatus
        get() = ErrorStatus(message)

}
