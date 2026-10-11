package com.selfcontrol.shortsblocker

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class ShortsBlockerService : AccessibilityService() {

    companion object {
        const val YOUTUBE_PKG = "com.google.android.youtube"
        const val INSTAGRAM_PKG = "com.instagram.android"

        // Warning ke baad app khud band hone se pehle itna grace time (seconds)
        const val GRACE_PERIOD_SECONDS = 15
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isInShortsOrReels = false
    private var warningTriggered = false
    private var graceSecondsLeft = GRACE_PERIOD_SECONDS
    private var overlayHelper: OverlayHelper? = null

    private val tickRunnable = object : Runnable {
        override fun run() {
            handleTick()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlayHelper = OverlayHelper(this)
        handler.post(tickRunnable)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (pkg != YOUTUBE_PKG && pkg != INSTAGRAM_PKG) {
            isInShortsOrReels = false
            return
        }

        val root = rootInActiveWindow
        val inShorts = AccessibilityUtils.containsAnyViewId(root, shortsReelsIds(pkg)) ||
                AccessibilityUtils.containsAnyText(root, listOf("Shorts", "Reels", "Reel"))
        isInShortsOrReels = inShorts

        // IMPORTANT: Sirf Shorts/Reels screen ko block karte hain, poori app ko nahi.
        // Isse YouTube par normal (padhai wale) video hamesha chalte rahenge,
        // sirf Shorts tab access karte hi turant wapas bhej diya jayega.
        if (inShorts && UsageTracker.isBlockedToday(this)) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            overlayHelper?.showBlockedMessage()
            isInShortsOrReels = false
        }
    }

    private fun shortsReelsIds(pkg: String): List<String> {
        return if (pkg == YOUTUBE_PKG) {
            listOf(
                "com.google.android.youtube:id/reel_player_page_container",
                "com.google.android.youtube:id/reel_recycler",
                "com.google.android.youtube:id/shorts_container"
            )
        } else {
            listOf(
                "com.instagram.android:id/clips_viewer_view_pager",
                "com.instagram.android:id/clips_swipe_refresh_container",
                "com.instagram.android:id/reels_tray_container"
            )
        }
    }

    private fun handleTick() {
        if (UsageTracker.isBlockedToday(this)) return

        if (!isInShortsOrReels) {
            // User ne Shorts/Reels chhod diya to grace period cancel kar do
            if (warningTriggered) {
                warningTriggered = false
                graceSecondsLeft = GRACE_PERIOD_SECONDS
                overlayHelper?.dismissWarning()
            }
            return
        }

        // User abhi Shorts/Reels dekh raha hai -> waqt gino
        UsageTracker.addSecond(this)
        val secondsToday = UsageTracker.getSecondsToday(this)

        if (secondsToday >= UsageTracker.DAILY_LIMIT_SECONDS && !warningTriggered) {
            warningTriggered = true
            graceSecondsLeft = GRACE_PERIOD_SECONDS
            triggerWarning()
            return
        }

        if (warningTriggered) {
            graceSecondsLeft--
            if (graceSecondsLeft <= 0) {
                blockForToday()
            }
        }
    }

    private fun triggerWarning() {
        overlayHelper?.showWarning()
    }

    private fun blockForToday() {
        UsageTracker.setBlockedToday(this, true)
        warningTriggered = false
        overlayHelper?.dismissWarning()
        overlayHelper?.showBlockedMessage()
        // Sirf Shorts/Reels se bahar nikaalte hain, poori app band nahi karte -
        // taaki YouTube par normal padhai wale video dekhna band na ho.
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tickRunnable)
        overlayHelper?.cleanup()
    }
}
