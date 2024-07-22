package org.archipanion.mw.sevice.controller


import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.archipanion.mw.sevice.model.result.QueryResult
import org.archipanion.mw.sevice.util.serialization.KotlinxJsonMapper


class QueryService(val basePath: String = "http://localhost:7070") {


    fun postQuery(schema: String, payload: String): QueryResult? {
        val client: OkHttpClient = OkHttpClient()

        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), payload
        )

        val request = Request.Builder()
            .url("$basePath/api/$schema/query")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()

        val result = KotlinxJsonMapper.fromJsonString<QueryResult>(response.body()!!.string(), QueryResult::class.java)
        return result
    }
}