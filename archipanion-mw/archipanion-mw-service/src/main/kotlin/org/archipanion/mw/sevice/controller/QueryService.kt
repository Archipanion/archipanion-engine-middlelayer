package org.archipanion.mw.sevice.controller


import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import org.archipanion.mw.sevice.model.result.QueryResult

import org.vitrivr.engine.query.model.api.InformationNeedDescription


class QueryService(val basePath: String = "http://localhost:7070") {


    fun postQuery(schema: String, payload: String): QueryResult? {
        val client: OkHttpClient = OkHttpClient()

        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), payload
        )

        val request = Request.Builder()
            .url("http://localhost:7070/api/MVK/query")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val objectMapper: ObjectMapper = ObjectMapper()
        val result = objectMapper.readValue(response.body()?.string(), QueryResult::class.java)
        return result
    }
}