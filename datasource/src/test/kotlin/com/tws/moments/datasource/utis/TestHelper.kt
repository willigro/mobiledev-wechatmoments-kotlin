package com.tws.moments.datasource.utis

import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.SenderBean
import com.tws.moments.datasource.api.entry.TweetBeanApi
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