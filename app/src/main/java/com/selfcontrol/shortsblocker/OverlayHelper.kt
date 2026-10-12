package com.selfcontrol.shortsblocker

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

class OverlayHelper(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var warningView: TextView? = null
    private val handler = Handler(Looper.getMainLooper())

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE
    }

    fun showWarning() {
        handler.post {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(
                    context,
                    "Your Shorts/Reels limit for today has ended. Please close this now.",
                    Toast.LENGTH_LONG
                ).show()
                return@post
            }
            if (warningView != null) return@post
            val tv = TextView(context).apply {
                text = "Your Shorts/Reels limit for today has ended.\nPlease close this now."
                setBackgroundColor(Color.parseColor("#DD000000"))
                setTextColor(Color.WHITE)
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(48, 48, 48, 48)
            }
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP
            try {
                windowManager.addView(tv, params)
                warningView = tv
            } catch (e: Exception) {
                // overlay permission missing or other issue - silently ignore
            }
        }
    }

    fun dismissWarning() {
        handler.post {
            warningView?.let {
                try {
                    windowManager.removeView(it)
                } catch (e: Exception) {
                }
                warningView = null
            }
        }
    }

    fun showBlockedMessage() {
        handler.post {
            Toast.makeText(
                context,
                "Your Shorts/Reels limit for today has ended. Try again tomorrow.",
                Toast.LENGTH_LONG
            ).show()
        }
        dismissWarning()
    }

    fun showProtectedMessage() {
        handler.post {
            Toast.makeText(
                context,
                "Shorts Blocker protected hai. Isko band/uninstall karne ke liye app mein 24 ghante ka unlock request karo.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun cleanup() {
        dismissWarning()
    }
}
