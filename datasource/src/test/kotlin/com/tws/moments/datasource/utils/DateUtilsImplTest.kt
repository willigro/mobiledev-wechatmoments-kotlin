package com.tws.moments.datasource.utils

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertNull
import org.junit.Test

class DateUtilsImplTest {

    @Test
    fun `formatDateFromServerToTimeLapsed with null date`() {
        assertNull(DateUtilsImpl(RealAppClock()).formatDateFromServerToTimeLapsed(null))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, difference is less then 24 hours, 4 hours ago`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-27 08:18:00")

        assertThat(result, `is`("4 hours ago"))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, difference is equals to 24 hours`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-26 12:18:00")

        assertThat(result, `is`("24 hours ago"))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, difference is bigger then to 24 hours, get representative`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-22 12:18:00")

        assertThat(result, `is`("2024-01-22 at 12:18:00"))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, retrieve 1 hour ago`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-27 11:18:00")

        assertThat(result, `is`("1 hour ago"))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, retrieve 2 hours ago`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-27 10:18:00")

        assertThat(result, `is`("2 hours ago"))
    }

    @Test
    fun `formatDateFromServerToTimeLapsed, retrieve 2 hours and 18 minutes ago`() {
        val clock = mockk<AppClock>()

        //  27 Jan 2024 around 12:18:00
        val mockedNow = 1706368680000

        every { clock.now() } returns mockedNow

        val result = DateUtilsImpl(clock).formatDateFromServerToTimeLapsed("2024-01-27 10:00:00")

        assertThat(result, `is`("2 hours ago"))
    }
}