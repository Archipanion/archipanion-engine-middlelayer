package org.archipanion.Model.Config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

/**
 * Configuration regarding the RESTful API.
 *
 * @author Ralph Gasser
 * @version 1.0.0
 */
@Serializable
data class ApiConfig (

    /** Port used by the RESTful API. */
    val port: Int = 7070,

)
