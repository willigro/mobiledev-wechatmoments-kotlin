package com.tws.moments.datasource.test_utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.SenderBean
import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.usecase.helpers.ResultUC
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue


fun <T> List<T>?.assertSize(size: Int): List<T> {
    assertEquals(size, this!!.size)
    return this
}

fun <T> List<T>?.assertNotEmpty(): List<T> {
    assertTrue(this!!.isNotEmpty())
    return this
}

fun <T> List<T>?.assertInstance(type: Class<T>, index: Int = 0): List<T> {
    assertThat(this!![index], instanceOf(type))
    return this
}

fun <T> List<T>?.assert(
    index: Int = 0,
    callback: (T) -> Unit,
): List<T> {
    callback(
        this!![index]
    )
    return this
}

fun <T> ResultUC<T>.assertFailure(): ResultUC<T> {
    assertTrue(isFailure)
    return this
}

fun <T> ResultUC<T>.assertSuccess(): ResultUC<T> {
    assertTrue(isSuccess)
    return this
}

fun mockSenderBean(
    nick: String? = "nick",
    username: String? = "username",
    avatar: String? = "avatar",
) = SenderBean(
    nick = nick,
    username = username,
    avatar = avatar,
)

fun mockCommentBean(
    content: String? = "comment 1",
) = CommentsBean(
    content = content,
)

fun mockTweetBeanApi(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
) = TweetBeanApi(
    content = content,
    sender = sender,
)

fun mockTweetBean(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
) = TweetBean(
    content = content,
    sender = sender,
)

fun mockTweetBeanCommented(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
    comments: SnapshotStateList<CommentsBean> = mutableStateListOf(
        mockCommentBean(),
    ),
) = TweetBean(
    content = content,
    sender = sender,
    comments = comments,
)

fun mockTweetBeanApiError(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
) = TweetBeanApi(
    content = content,
    sender = sender,
    error = "Has error",
)

fun mockTweetBeanApiUnknownError(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
) = TweetBeanApi(
    content = content,
    sender = sender,
    unknownError = "Has unknownError",
)

fun mockTweetBeanApiCommented(
    content: String? = "content",
    sender: SenderBean? = mockSenderBean(),
    comments: List<CommentsBean>? = arrayListOf(
        mockCommentBean(),
    ),
) = TweetBeanApi(
    content = content,
    sender = sender,
    comments = comments,
)