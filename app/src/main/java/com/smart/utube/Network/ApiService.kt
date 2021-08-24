package com.smart.utube.Network

import android.content.SharedPreferences
import com.smart.utube.DataClasses.trendingVideosHome
import com.smart.utube.RelatedVideoDataset.relatedVideos
import com.smart.utube.SearchQuery.SearchQueryHome
import com.smart.utube.VideoStatistics.StaticHome
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("videos?part=snippet%2CcontentDetails%2Cstatistics&chart=mostPopular&maxResults=50&regionCode=IN&key=APIKEY")
    suspend fun getTrending () : trendingVideosHome

    @GET("videos?part=snippet%2CcontentDetails%2Cstatistics&chart=mostPopular&pageToken=CAUQAA&maxResults=50&regionCode=IN&key=APIKEY")
    suspend fun getNextTrendingPage (
            @Query("pageToken") nextToke : String
    ) : trendingVideosHome

    @GET("search?part=snippet&maxResults=50&q=name&key=APIKEY")
    suspend fun getSearchResult (
            @Query("q") Query: String) : SearchQueryHome

    @GET("search?part=snippet&maxResults=50&pageToken=CAUQAA&q=name&key=APIKEY")
    suspend fun getSeaerchNextResult (
            @Query("pageToken") NextToken : String,
            @Query("q") Query: String
    ) : SearchQueryHome

    @GET("search?part=snippet&channelId=UClmlqOOktUTpfW0EkTiqSjQ&maxResults=20&order=rating&pageToken=CAUQAA&q=News%20in%20hindi&regionCode=IN&key=APIKEY")
    suspend fun ChennelVideos(
            @Query("channelId") CheennelId : String,
            @Query("pageToken") nextPage : String
    ) : SearchQueryHome

    @GET("videos?part=snippet%2CcontentDetails%2Cstatistics&id=Ks-_Mh1QhMc&key=APIKEY")
    suspend fun getStatistics(
                              @Query("id") videoId : String) : StaticHome

    @GET("search?part=snippet&maxResults=40&relatedToVideoId=Ks-_Mh1QhMc&type=video&key=APIKEY")
    suspend fun relatedVideos(
            @Query("relatedToVideoId") videoId : String) : relatedVideos

    @GET("search?part=snippet&maxResults=40&pageToken=CAUQAA&relatedToVideoId=Ks-_Mh1QhMc&type=APIKEY")
    suspend fun nextPageRelatedVideos(
            @Query("pageToken") nextPage: String,
            @Query("relatedToVideoId")videoId : String
    ) : relatedVideos
}