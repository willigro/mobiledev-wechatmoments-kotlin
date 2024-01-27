package com.tws.moments.datasource.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs

const val ONE_DAY_IN_MILLIS = 1_000L * 60 * 60 * 24

interface DateUtils {
    fun formatDateFromServerToTimeLapsed(string: String?): String?
}

@SuppressLint("SimpleDateFormat")
class DateUtilsImpl @Inject constructor(
    private val clock: AppClock,
) : DateUtils {
    private val serverFormat = "yyyy-MM-dd HH:mm:ss"
    private val representativeFormat = "yyyy-MM-dd 'at' HH:mm:ss"

    override fun formatDateFromServerToTimeLapsed(string: String?): String? {
        if (string == null) return null

        val date = SimpleDateFormat(serverFormat).parse(string) ?: return null

        val now = Calendar.getInstance().apply {
            timeInMillis = clock.now()
        }

        val diff = abs(date.time - now.timeInMillis)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        // TODO, get text from strings.xml
        return if (diff > ONE_DAY_IN_MILLIS) {
            SimpleDateFormat(representativeFormat).format(date.time)
        } else {
            when (hours) {
                0L -> {
                    "$minutes minutes ago"
                }

                1L -> {
                    "1 hour ago"
                }

                else -> "$hours hours ago"
            }
        }
    }
}