package com.minotawr.storyapp.utils

import android.content.Context
import android.util.Patterns
import com.minotawr.storyapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object CommonUtils {
    private const val responseTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    fun String?.isValidEmail(): Boolean =
        if (this == null) false else
            Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun String?.isValidPassword(): Boolean =
        this?.contains("""(?=.*[A-Z])(?=.*\d).{8,}""".toRegex()) ?: false

    fun String?.toDate(): Date? {
        if (this == null) return null

        return try {
            val format = SimpleDateFormat(responseTimeFormat, Locale.US)
            format.parse(this)
        } catch (e: Exception) {
            null
        }
    }

    fun Date.getAppropriatePostTime(context: Context): String {
        val now = Date()
        val dateDifference = now.time - this.time

        val daysFromNow = TimeUnit.DAYS.convert(dateDifference, TimeUnit.MILLISECONDS).toInt()
        return if (daysFromNow > 30) {
            val monthsFromNow = daysFromNow / 30
            context.getString(R.string.story_detail_months, monthsFromNow)
        } else if (daysFromNow == 0) {
            val hoursFromNow = TimeUnit.HOURS.convert(dateDifference, TimeUnit.MILLISECONDS).toInt()
            if (hoursFromNow < 0) {
                val minutesFromNow = TimeUnit.MINUTES.convert(dateDifference, TimeUnit.MILLISECONDS).toInt()
                if (minutesFromNow < 0) context.getString(R.string.story_detail_seconds)
                else context.getString(R.string.story_detail_minutes, minutesFromNow)
            } else context.getString(R.string.story_detail_hours, hoursFromNow)
        } else
            context.getString(R.string.story_detail_days, daysFromNow)
    }
}