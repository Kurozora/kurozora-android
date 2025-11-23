package com.seloreis.kurozora.core.util

import kotlinx.datetime.*

/**
 * Kotlin Multiplatform Instant Utilities
 *
 * Provides timezone-aware conversion, formatting, human-readable strings,
 * relative time, and common helper functions for Instant objects.
 * Works on Android, iOS, Desktop, and Web.
 */

object InstantUtils {

    /**
     * Convert Instant to LocalDate in a given TimeZone
     */
    fun Instant.toLocalDate(tz: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
        this.toLocalDateTime(tz).date

    /**
     * Convert Instant to LocalTime in a given TimeZone
     */
    fun Instant.toLocalTime(tz: TimeZone = TimeZone.currentSystemDefault()): LocalTime =
        this.toLocalDateTime(tz).time

    /**
     * Convert Instant to LocalDateTime in a given TimeZone
     */
    fun Instant.asLocalDateTime(tz: TimeZone): LocalDateTime =
        this.toLocalDateTime(tz)

    /**
     * Format Instant as "HH:mm" string in a given TimeZone
     */
    fun Instant.toHourMinute(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = this.toLocalDateTime(tz)
        return "%02d:%02d".format(dt.hour, dt.minute)
    }

    /**
     * Format Instant as "HH:mm:ss" string in a given TimeZone
     */
    fun Instant.toTimeWithSeconds(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = this.toLocalDateTime(tz)
        return "%02d:%02d:%02d".format(dt.hour, dt.minute, dt.second)
    }

    /**
     * Format Instant as "dd.MM.yyyy" string in a given TimeZone
     */
    fun Instant.toDateString(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val d = this.toLocalDateTime(tz).date
        return "%02d.%02d.%04d".format(d.dayOfMonth, d.monthNumber, d.year)
    }

    /**
     * Format Instant as "dd.MM.yyyy HH:mm" string in a given TimeZone
     */
    fun Instant.toDateTimeString(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = this.toLocalDateTime(tz)
        return "%02d.%02d.%04d %02d:%02d".format(
            dt.date.dayOfMonth,
            dt.date.monthNumber,
            dt.date.year,
            dt.hour,
            dt.minute
        )
    }

    /**
     * Check if the Instant is today in the given TimeZone
     */
    fun Instant.isToday(tz: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val today = Clock.System.now().toLocalDate(tz)
        return this.toLocalDate(tz) == today
    }

    /**
     * Check if the Instant is tomorrow in the given TimeZone
     */
    fun Instant.isTomorrow(tz: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val tomorrow = Clock.System.now().toLocalDate(tz).plus(DatePeriod(days = 1))
        return this.toLocalDate(tz) == tomorrow
    }

    /**
     * Check if the Instant is yesterday in the given TimeZone
     */
    fun Instant.isYesterday(tz: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val yesterday = Clock.System.now().toLocalDate(tz).minus(DatePeriod(days = 1))
        return this.toLocalDate(tz) == yesterday
    }

    /**
     * Human-readable date-time string
     * Example outputs: "Today 14:30", "Tomorrow 09:15", "2025-11-14 17:00"
     */
    fun Instant.toHumanReadable(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = this.toLocalDateTime(tz)
        val today = Clock.System.now().toLocalDate(tz)

        return when (dt.date) {
            today -> "Today ${"%02d:%02d".format(dt.hour, dt.minute)}"
            today.plus(DatePeriod(days = 1)) -> "Tomorrow ${"%02d:%02d".format(dt.hour, dt.minute)}"
            today.minus(DatePeriod(days = 1)) -> "Yesterday ${"%02d:%02d".format(dt.hour, dt.minute)}"
            else -> dt.date.toString() + " " + dt.time.toString().take(5)
        }
    }

    /**
     * Relative time string
     * Example outputs: "5 minutes ago", "2 hours later", "Tomorrow"
     */
    fun Instant.relativeToNow(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        val now = Clock.System.now()
        val diff = this - now

        val minutes = diff.inWholeMinutes
        val hours = diff.inWholeHours
        val days = diff.inWholeDays

        return when {
            minutes == 0L -> "Now"
            minutes in 1..59 -> "$minutes minutes later"
            hours in 1..23 -> "$hours hours later"
            days == 1L -> "Tomorrow"
            days > 1 -> "$days days later"

            minutes in -59..-1 -> "${-minutes} minutes ago"
            hours in -23..-1 -> "${-hours} hours ago"
            days == -1L -> "Yesterday"
            days < -1 -> "${-days} days ago"

            else -> this.toDateTimeString(tz)
        }
    }

    /**
     * Return the day of the week as string (Monday, Tuesday, etc.)
     */
    fun Instant.getDayOfWeek(tz: TimeZone = TimeZone.currentSystemDefault()): String {
        return this.toLocalDateTime(tz).dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    /**
     * Convert Instant to ISO 8601 string in UTC
     */
    fun Instant.toIsoUTC(): String = this.toString()

    /**
     * Convert Instant to user-specified TimeZone string
     * Format example: "2025-11-14 17:00"
     */
    fun Instant.toFormattedString(tz: TimeZone = TimeZone.currentSystemDefault(), includeTime: Boolean = true): String {
        val dt = this.toLocalDateTime(tz)
        return if (includeTime) {
            "%04d-%02d-%02d %02d:%02d".format(
                dt.date.year, dt.date.monthNumber, dt.date.dayOfMonth,
                dt.hour, dt.minute
            )
        } else {
            "%04d-%02d-%02d".format(dt.date.year, dt.date.monthNumber, dt.date.dayOfMonth)
        }
    }
}
