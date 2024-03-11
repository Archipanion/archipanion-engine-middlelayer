package org.archipanion.Model.Config

import kotlinx.serialization.Serializable

/**
 * Configuration regarding the RESTful API.
 *
 * @author Ralph Gasser
 * @version 1.0.0
 */
@Serializable
data class ServiceConfig (

    /** Ip forConfig **/
    val ip: String = "localhost",
    /** Endpoint for specific schema. Empty for default **/
    val schema: String = "",
    /** Port used by the RESTful API. */
    val port: Int = 7070,
)
