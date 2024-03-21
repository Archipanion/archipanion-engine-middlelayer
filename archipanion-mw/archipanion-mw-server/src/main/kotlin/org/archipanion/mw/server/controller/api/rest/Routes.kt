package org.archipanion.mw.server.controller.api.rest

import io.javalin.apibuilder.ApiBuilder
import org.archipanion.mw.server.model.config.ApiConfig


class Routes {

    /**
     * Configures all the API routes.
     *
     * @param config The [ApiConfig] used for persistence.
     * @param manager The [SchemaManager] used for persistence.
     */
    companion object {
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
    }
}