package com.applimiter.app

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import java.util.Calendar

class BlockerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return
        val limits = LimitsStore.getLimits(this)
        val limitMinutes = limits[packageName] ?: return
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val now = System.currentTimeMillis()
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, now)
        val usedMs = stats?.filter { it.packageName == packageName }
            ?.sumOf { it.totalTimeInForeground } ?: 0L
        val usedMinutes = (usedMs / 60000).toInt()
        if (usedMinutes >= limitMinutes) {
            try {
                val appName = packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(packageName, 0)
                ).toString()
                val intent = Intent(this, BlockActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("appName", appName)
                intent.putExtra("minutes", limitMinutes)
                startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    override fun onInterrupt() {}
}
