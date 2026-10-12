package com.selfcontrol.shortsblocker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var statusText: TextView
    private lateinit var usageText: TextView

    private val refreshRunnable = object : Runnable {
        override fun run() {
            refreshUi()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        usageText = findViewById(R.id.usageText)

        findViewById<Button>(R.id.btnEnableAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.btnEnableOverlay).setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val expected = "$packageName/${ShortsBlockerService::class.java.name}"
        return enabledServices.split(":").any { it.equals(expected, ignoreCase = true) }
    }

    private fun refreshUi() {
        val enabled = isAccessibilityServiceEnabled()
        val blocked = UsageTracker.isBlockedToday(this)
        val seconds = UsageTracker.getSecondsToday(this)
        val minutes = seconds / 60
        val remSeconds = seconds % 60

        statusText.text = when {
            !enabled -> "Accessibility Service: BAND hai. Neeche button se ON karo."
            blocked -> "Aaj YouTube/Instagram BLOCK hai (limit paar ho gaya)."
            else -> "Accessibility Service: ON hai. Tracking chalu hai."
        }
        usageText.text = "Aaj ka Shorts/Reels time: $minutes min $remSeconds sec / 60 min\n\nDetect: ${ShortsBlockerService.lastDetect}"
    }
}
