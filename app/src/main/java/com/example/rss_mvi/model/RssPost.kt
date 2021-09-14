package com.example.rss_mvi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity
//@Root(name = "item", strict = false)
//data class RssPost @JvmOverloads constructor(
//    @PrimaryKey(autoGenerate = true)
//    var id: Int?,
//
//    @ColumnInfo(name = "title")
//    @field:Element(name = "title", required = false)
//    @param:Element(name = "title", required = false)
//    var title: String = "",
//
//    @ColumnInfo(name = "description")
//    @field:Element(name = "description", required = false)
//    @param:Element(name = "description", required = false)
//    var description: String = "",
//)

@Entity
data class RssPost(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "feed_id") var feedId: Int? = null,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?
)
