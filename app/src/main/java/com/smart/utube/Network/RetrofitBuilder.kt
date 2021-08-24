package com.smart.utube.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private val retrofit by lazy {
       Retrofit.Builder()
               .baseUrl("https://youtube.googleapis.com/youtube/v3/")
               .addConverterFactory(GsonConverterFactory.create())
               .build()
    }


    val apiTrending : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiSearch : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiSearchNext : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apinextTrendingPage : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiChannelVideos : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiStatiticsVideo : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiRelatedVideos : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val apiNextPageRelatedVideos : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}


