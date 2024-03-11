package org.archipanion.controller.api.rest

import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.Context
import org.archipanion.Controller.api.Rest.Cineast.findSegments
import org.archipanion.Model.Config.ApiConfig

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
                    ApiBuilder.path("segments") {
                        ApiBuilder.get("{id}") { ctx -> findSegments(ctx) }
                    }
                }
            }
        }
    }
}