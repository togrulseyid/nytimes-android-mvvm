package com.toghrulseyidov.apps.nytimes.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName


@Entity
data class NewsList(
    val status: String,
    val copyright: String,
    @Embedded
    @SerializedName("response")
    val response: Response?,
    @Embedded
    val meta: Meta?
)

data class Response(
    @SerializedName("docs")
    val docs: List<Article>,
    @SerializedName("meta")
    @Ignore val meta: Meta
)

data class Meta(val hits: Int, val offset: Int, val time: Int)
