package com.tws.moments.datasource.shared.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.ImagesBean
import com.tws.moments.datasource.api.entry.SenderBean
import java.util.UUID

@Stable
data class TweetBean(
    @Transient
    val id: UUID = UUID.randomUUID(),
    val content: String? = null,
    val sender: SenderBean? = null,
    val images: List<ImagesBean>? = null,
    val comments: SnapshotStateList<CommentsBean> = mutableStateListOf(),
    val error: String? = null,
    @SerializedName("unknown error")
    val unknownError: String? = null,
) {
    fun noErrorAndWithContent() =
        error.isNullOrEmpty() && unknownError.isNullOrEmpty() && content.isNullOrEmpty().not()
}