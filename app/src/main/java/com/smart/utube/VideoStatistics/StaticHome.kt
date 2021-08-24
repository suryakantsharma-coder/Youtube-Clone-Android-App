package com.smart.utube.VideoStatistics

data class StaticHome(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
)