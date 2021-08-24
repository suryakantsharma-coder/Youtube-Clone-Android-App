package com.smart.utube.DataClasses

data class trendingVideosHome(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
)