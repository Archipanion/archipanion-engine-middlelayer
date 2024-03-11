package org.archipanion.mw.server.model.config

import kotlinx.serialization.Serializable

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
