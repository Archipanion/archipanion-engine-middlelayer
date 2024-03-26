package org.archipanion.mw.server.controller.api.rest

import io.javalin.apibuilder.ApiBuilder
import org.archipanion.mw.server.model.config.ApiConfig


fun configureApiRoutes(config: ApiConfig) {
    // Retrieval paths
    ApiBuilder.path("api") {
        ApiBuilder.path("v1") {
            ApiBuilder.path("{schema}") {
                ApiBuilder.path("{pipeline}") {
                    ApiBuilder.post("query") { ctx -> findSegments(ctx) }
                }
            }
        }
    }
}