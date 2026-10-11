package com.selfcontrol.shortsblocker

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * Har din ka Shorts + Reels combined usage store karta hai.
 * Date badalte hi (midnight ke baad) apne aap reset ho jata hai.
 */
object UsageTracker {
    private const val PREFS = "shorts_blocker_prefs"
    private const val KEY_DATE = "usage_date"
    private const val KEY_SECONDS = "usage_seconds"
    private const val KEY_BLOCKED = "blocked_today"

    // Daily limit: 1 ghanta (chahe to yahan change kar sakte ho)
    const val DAILY_LIMIT_SECONDS = 60 * 60

    private fun todayKey(): String =
        SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun ensureFreshDay(ctx: Context) {
        val p = prefs(ctx)
        val savedDate = p.getString(KEY_DATE, "")
        if (savedDate != todayKey()) {
            p.edit()
                .putString(KEY_DATE, todayKey())
                .putInt(KEY_SECONDS, 0)
                .putBoolean(KEY_BLOCKED, false)
                .apply()
        }
    }

    fun addSecond(ctx: Context) {
        ensureFreshDay(ctx)
        val p = prefs(ctx)
        val current = p.getInt(KEY_SECONDS, 0) + 1
        p.edit().putInt(KEY_SECONDS, current).apply()
    }

    fun getSecondsToday(ctx: Context): Int {
        ensureFreshDay(ctx)
        return prefs(ctx).getInt(KEY_SECONDS, 0)
    }

    fun isBlockedToday(ctx: Context): Boolean {
        ensureFreshDay(ctx)
        return prefs(ctx).getBoolean(KEY_BLOCKED, false)
    }

    fun setBlockedToday(ctx: Context, blocked: Boolean) {
        ensureFreshDay(ctx)
        prefs(ctx).edit().putBoolean(KEY_BLOCKED, blocked).apply()
    }
}
