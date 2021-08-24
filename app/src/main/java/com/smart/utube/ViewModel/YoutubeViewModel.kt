package com.smart.utube.ViewModel

import android.util.Log
import androidx.lifecycle.*
import com.smart.utube.DataClasses.Statistics
import com.smart.utube.DataClasses.trendingVideosHome
import com.smart.utube.Network.Repositry
import com.smart.utube.Network.RetrofitBuilder
import com.smart.utube.RelatedVideoDataset.relatedVideos
import com.smart.utube.SearchQuery.SearchQueryHome
import com.smart.utube.VideoStatistics.StaticHome
import kotlinx.coroutines.launch
import java.lang.Exception

class YoutubeViewModel(val repositry: Repositry) : ViewModel() {

    var trendingVideosList = MutableLiveData<trendingVideosHome>()
    var searchList = MutableLiveData<SearchQueryHome>()
    var statisticsVideos = MutableLiveData<StaticHome>()
    var listofRelatedVideos = MutableLiveData<relatedVideos>()

    fun getTrendingVideos () {

        viewModelScope.launch {
            try {
                val response = repositry.getTrandingVideos()
                trendingVideosList.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getNextPageTrendingVideos ( nextPageToke : String) {

        viewModelScope.launch {
            try {
                val response = repositry.getNextTrendingPage(nextPageToke)
                trendingVideosList.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getSearchVideos (Query : String) {
        viewModelScope.launch {
            try {
                val response = repositry.getSearchList(Query)
                searchList.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getSearchNextPage ( NextPage : String , Query: String) {
        viewModelScope.launch {
            try {
                val response = repositry.getSearchNextPage(NextPage,Query)
                searchList.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }


    fun getStaticVideoId (videoId : String) {
        viewModelScope.launch {
            try {
                val response = repositry.getStatisticsDetails(videoId)
                statisticsVideos.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getRelatedVideos (videoId : String) {
        viewModelScope.launch {
            try {
                val response = repositry.getRelatedVideos(videoId)
                listofRelatedVideos.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getNextPageRealatedVideos(NextToken : String ,Query: String) {
        viewModelScope.launch {
            try {
                val response = repositry.getNextPageRelatedVideos(NextToken,Query)
                listofRelatedVideos.value = response
            }catch (e : Exception){
                Log.d("Exception"," : $e")
            }
        }
    }

    fun getChannelList(ChannelId : String , NextPage: String) {
            viewModelScope.launch {
                try {
                    val response = repositry.getChannelList(ChannelId, NextPage)
                    searchList.value = response
                }catch (e : Exception){
                    Log.d("Exception"," : $e")
                }
        }
    }
}

