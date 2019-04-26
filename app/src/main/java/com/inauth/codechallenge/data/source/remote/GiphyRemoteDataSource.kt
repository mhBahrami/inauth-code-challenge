package com.inauth.codechallenge.data.source.remote

import androidx.annotation.VisibleForTesting
import com.inauth.codechallenge.data.SearchResult
import com.inauth.codechallenge.data.source.AppDataSource
import com.inauth.codechallenge.util.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiphyRemoteDataSource private constructor (
    private val appExecutors: AppExecutors,
    private val giphyApiService: GiphyApiService
) : AppDataSource.GiphyDataSource {

    /**
     * Note: [AppDataSource.LoadPrettyJsonCallback] would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun onSearch(query: String, callback: AppDataSource.LoadPrettyJsonCallback) {
        try {
            appExecutors.networkIO.execute {
                val call =
                    giphyApiService.search(
                        query,
                        GiphyApiService.API_KEY,
                        GiphyApiService.DEFAULT_LIMIT
                    )
                call.enqueue(object : Callback<SearchResult> {
                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        // We can improve it by detecting the issue and displaying
                        // the appropriate message to the user
                        callback.onDataNotAvailable()
                    }

                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        val prettyJson =
                            GiphyApiService.getInstanceGsonBuilder()
                                .setPrettyPrinting()
                                .create()
                                .toJson(response.body())
                        prettyJson.let {
                            callback.onLoadedPrettyJson(it)
                        }
                    }
                })
            }
        }
        catch (e: Exception)
        {
            // We can improve it by detecting the issue and displaying
            // the appropriate message to the user
            callback.onDataNotAvailable()
        }
    }

    companion object {
        private var INSTANCE: GiphyRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, giphyApiService: GiphyApiService): GiphyRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(GiphyRemoteDataSource::javaClass) {
                    INSTANCE =
                        GiphyRemoteDataSource(appExecutors, giphyApiService)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

}