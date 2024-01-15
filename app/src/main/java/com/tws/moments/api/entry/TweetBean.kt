package com.tws.moments.api.entry

data class TweetBean(
    val content: String? = null,
    val sender: SenderBean? = null,
    val images: List<ImagesBean>? = null,
    val comments: List<CommentsBean>? = null,
)