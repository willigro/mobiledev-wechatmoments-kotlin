package com.tws.moments.datasource.usecase

import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.usecase.helpers.CoroutineDispatcherHelper
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class) // TODO remove mockito?
class MomentsUseCaseTest {

    private val iDispatcher: IDispatcher = CoroutineDispatcherHelper()
    private lateinit var momentRepository: MomentRepository
    private lateinit var momentsUseCase: MomentsUseCase

    @Before
    fun setup() {
        momentRepository = mockk<MomentRepository>()

        momentsUseCase = MomentsUseCaseImpl(
            iDispatcher = iDispatcher,
            repository = momentRepository,
        )
    }

    @Test
    fun `fetch user, throws exception, return null`() = runTest {
        coEvery {
            momentRepository.fetchUser()
        } throws Exception()

        val result = momentsUseCase.fetchUser()

        assertNull(result)
    }

    @Test
    fun `fetch user, retrieve user`() = runTest {
        val userName = "username"
        val nick = "nick"
        val avatar = "avatar"
        val profileImage = "profileImage"

        coEvery {
            momentRepository.fetchUser()
        } returns UserBean(
            username = userName,
            nick = nick,
            avatar = avatar,
            profileImage = profileImage,
        )

        with(momentsUseCase.fetchUser()!!) {
            assertEquals(userName, this.username)
            assertEquals(nick, this.nick)
            assertEquals(avatar, this.avatar)
            assertEquals(profileImage, this.profileImage)
        }
    }
}