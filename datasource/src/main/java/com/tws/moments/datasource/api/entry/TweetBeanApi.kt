package com.tws.moments.datasource.api.entry

import com.google.gson.annotations.SerializedName

data class TweetBeanServer(
    val content: String? = null,
    val sender: SenderBean? = null,
    val images: List<ImagesBean>? = null,
    val comments: List<CommentsBean>? = null,
    val error: String? = null,
    @SerializedName("unknown error")
    val unknownError: String? = null,
)

