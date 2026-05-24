package com.domenechobiol.forocoches

import android.content.Context

class NotificationRepository(context: Context) {

    private val prefs = context.getSharedPreferences("fc_notifications", Context.MODE_PRIVATE)

    fun getLastPmCount(): Int = prefs.getInt("last_pm_count", -1)
    fun setLastPmCount(count: Int) { prefs.edit().putInt("last_pm_count", count).apply() }

    fun getLastNotifCount(): Int = prefs.getInt("last_notif_count", -1)
    fun setLastNotifCount(count: Int) { prefs.edit().putInt("last_notif_count", count).apply() }

    fun getFavoriteUsers(): Map<String, String> {
        val usernames = prefs.getStringSet("fav_users", emptySet()) ?: emptySet()
        return usernames.associateWith { prefs.getString("fav_uid_$it", "") ?: "" }
    }

    fun addFavoriteUser(username: String, userId: String) {
        val current = prefs.getStringSet("fav_users", emptySet())?.toMutableSet() ?: mutableSetOf()
        current.add(username)
        prefs.edit()
            .putStringSet("fav_users", current)
            .putString("fav_uid_$username", userId)
            .apply()
    }

    fun removeFavoriteUser(username: String) {
        val current = prefs.getStringSet("fav_users", emptySet())?.toMutableSet() ?: mutableSetOf()
        current.remove(username)
        prefs.edit()
            .putStringSet("fav_users", current)
            .remove("fav_uid_$username")
            .remove("last_thread_$username")
            .apply()
    }

    fun getLastSeenThreadId(username: String): String? = prefs.getString("last_thread_$username", null)
    fun setLastSeenThreadId(username: String, threadId: String) {
        prefs.edit().putString("last_thread_$username", threadId).apply()
    }
}
