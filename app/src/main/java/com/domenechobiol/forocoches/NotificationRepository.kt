package com.domenechobiol.forocoches

import android.content.Context

class NotificationRepository(context: Context) {

    private val prefs = context.getSharedPreferences("fc_notifications", Context.MODE_PRIVATE)

    fun getLastPmCount(): Int = prefs.getInt("last_pm_count", -1)
    fun setLastPmCount(count: Int) { prefs.edit().putInt("last_pm_count", count).apply() }

    fun getLastNotifCount(): Int = prefs.getInt("last_notif_count", -1)
    fun setLastNotifCount(count: Int) { prefs.edit().putInt("last_notif_count", count).apply() }


}
