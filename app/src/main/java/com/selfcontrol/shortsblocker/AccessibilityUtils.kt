package com.selfcontrol.shortsblocker

import android.view.accessibility.AccessibilityNodeInfo
import java.util.ArrayDeque

/**
 * YouTube/Instagram screen ke andar "Shorts" ya "Reels" dikh rahe hain ya nahi,
 * ye pata karne ke liye helper functions. Ye heuristic hai — agar YouTube/Instagram
 * apna internal view-id badal de to yahan ki id list update karni pad sakti hai.
 */
object AccessibilityUtils {

    fun containsAnyViewId(root: AccessibilityNodeInfo?, ids: List<String>): Boolean {
        if (root == null) return false
        for (id in ids) {
            val nodes = try {
                root.findAccessibilityNodeInfosByViewId(id)
            } catch (e: Exception) {
                null
            }
            if (!nodes.isNullOrEmpty()) return true
        }
        return false
    }

    fun containsAnyText(root: AccessibilityNodeInfo?, needles: List<String>, maxNodesToScan: Int = 400, selectedOnly: Boolean = false): Boolean {
        if (root == null) return false
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var scanned = 0
        while (queue.isNotEmpty() && scanned < maxNodesToScan) {
            val node = queue.poll() ?: continue
            scanned++
            if (selectedOnly && !node.isSelected) {
                for (i in 0 until node.childCount) { node.getChild(i)?.let { queue.add(it) } }
                continue
            }
            val text = node.text?.toString()
            val desc = node.contentDescription?.toString()
            for (needle in needles) {
                if (text?.contains(needle, ignoreCase = true) == true ||
                    desc?.contains(needle, ignoreCase = true) == true
                ) {
                    return true
                }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return false
    }
}
