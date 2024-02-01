package com.tws.moments.datasource.shared.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.SenderBean
import java.util.UUID

@Stable
data class TweetBean(
    val id: UUID = UUID.randomUUID(),
    val content: String? = null,
    val sender: SenderBean? = null,
    val imagesUrls: List<String>? = null,
    val comments: SnapshotStateList<CommentsBean> = mutableStateListOf(),
    val error: String? = null,
    val unknownError: String? = null,
    val time: String? = null,
    val isSendingComment: Boolean = false,
) {
    fun noErrorAndWithContent() =
        error.isNullOrEmpty() && unknownError.isNullOrEmpty() && content.isNullOrEmpty().not()
}