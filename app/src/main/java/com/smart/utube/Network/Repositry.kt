package com.smart.utube.Network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.smart.utube.DataClasses.trendingVideosHome
import com.smart.utube.RelatedVideoDataset.relatedVideos
import com.smart.utube.SearchQuery.SearchQueryHome
import com.smart.utube.VideoStatistics.StaticHome

class Repositry {

    suspend fun getTrandingVideos() : trendingVideosHome = RetrofitBuilder.apiTrending.getTrending()

    suspend fun getNextTrendingPage(netPageToken : String) : trendingVideosHome = RetrofitBuilder.apinextTrendingPage.getNextTrendingPage(netPageToken)

    suspend fun getSearchList(Query : String) : SearchQueryHome = RetrofitBuilder.apiSearch.getSearchResult(Query)

    suspend fun getSearchNextPage (NextToken : String ,Query: String) : SearchQueryHome = RetrofitBuilder.apiSearchNext.getSeaerchNextResult(NextToken,Query)

    suspend fun getChannelList(ChannelId : String, NextToken : String) : SearchQueryHome = RetrofitBuilder.apiChannelVideos.ChennelVideos(ChannelId, NextToken)

    suspend fun getStatisticsDetails(videoId : String) : StaticHome = RetrofitBuilder.apiStatiticsVideo.getStatistics(videoId)

    suspend fun getRelatedVideos(videoId : String) : relatedVideos = RetrofitBuilder.apiRelatedVideos.relatedVideos(videoId)

    suspend fun getNextPageRelatedVideos(NextToken : String ,Query: String) : relatedVideos = RetrofitBuilder.apiNextPageRelatedVideos.nextPageRelatedVideos(NextToken,Query)
}