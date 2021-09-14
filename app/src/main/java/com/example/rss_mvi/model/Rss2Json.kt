package com.example.rss_mvi.model

//@Root(name="rss", strict = false)
//data class XmlRssFeed @JvmOverloads constructor(
//    @Path("channel")
//    @field:Element(name = "title", required = false)
//    @param:Element(name = "title", required = false)
//    var channelTitle: String = "",
//
//    @Path("channel")
//    @field:ElementList(name = "item", inline = true, required = false)
//    @param:ElementList(name = "item", inline = true, required = false)
//    var articleList: List<RssPost> = listOf()
//)

data class Rss2Json(
    val items: MutableList<RssPost>,
)
