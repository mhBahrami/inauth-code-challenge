package com.inauth.codechallenge.data.source.remote

import com.inauth.codechallenge.data.SearchResult
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.IllegalArgumentException

interface GiphyApiService {

    @GET("/v1/gifs/search?")
    fun search(
        @Query("q") query: String,
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int): Call<SearchResult>


    companion object {
        const val DEFAULT_QUERY: String = "cat"
        const val API_KEY: String = "F4pm3uatkRluwVmXbRUWXQDyXS3Gh6e0"
        const val DEFAULT_LIMIT: Int = 1
        private const val BASE_URL: String = "http://api.giphy.com/"

        private var INSTANCE: GiphyApiService? = null
        private var INSTANCE_GSON_BUILDER: GsonBuilder? = null

        fun getInstance() : GiphyApiService =
            INSTANCE
                ?: synchronized(GiphyApiService::class.java)
            {
                INSTANCE ?: Retrofit.Builder()
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            getInstanceGsonBuilder().create()))
                    .baseUrl(BASE_URL)
                    .build()
                    .create(GiphyApiService::class.java)
                    .also { INSTANCE = it }
            }

        fun getInstanceGsonBuilder() : GsonBuilder =
            INSTANCE_GSON_BUILDER
                ?: synchronized(GsonBuilder::class.java)
            {
                INSTANCE_GSON_BUILDER ?: GsonBuilder()
                    .registerTypeAdapter(Boolean::class.java,
                        getBooleanAsIntAdapter()
                    )
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .also { INSTANCE_GSON_BUILDER = it }
            }

        private fun getBooleanAsIntAdapter(): TypeAdapter<Boolean>{
            return object : TypeAdapter<Boolean>() {
                override fun read(`in`: JsonReader?): Boolean {
                     `in`?.let {
                         return when (it.peek()) {
                             JsonToken.BOOLEAN -> `in`.nextBoolean()
                             JsonToken.NUMBER -> `in`.nextInt() != 0
                             JsonToken.STRING -> `in`.nextString().equals("1", ignoreCase = true)
                             else -> throw IllegalArgumentException()
                         }
                     }
                    throw IllegalArgumentException()
                }

                override fun write(out: JsonWriter?, value: Boolean?) {
                    if (value == null) {
                        out?.nullValue()
                    } else {
                        out?.value(value)
                    }
                }
            }
        }
    }
}