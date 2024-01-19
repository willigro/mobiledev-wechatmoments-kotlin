package com.tws.moments.api.entry

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class TweetBean(
    @Transient
    val id: UUID = UUID.randomUUID(),
    val content: String? = null,
    val sender: SenderBean? = null,
    val images: List<ImagesBean>? = null,
    val comments: List<CommentsBean>? = null,
    val error: String? = null,
    @SerializedName("unknown error")
    val unknownError: String? = null,
) {
    fun noErrorAndWithContent() = error.isNullOrEmpty() && unknownError.isNullOrEmpty() && content.isNullOrEmpty().not()
}