package com.domenechobiol.forocoches

import android.content.Context
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class SettingsBridge(private val repo: IgnoreListRepository, private val context: Context) {

    @JavascriptInterface
    fun getHideMode(): String = repo.getHideMode()

    @JavascriptInterface
    fun setHideMode(mode: String) {
        if (mode == "complete" || mode == "message") repo.setHideMode(mode)
    }

    @JavascriptInterface
    fun getIgnoredUsersJson(): String {
        val users = repo.getIgnoredUsers()
        if (users.isEmpty()) return "[]"
        return "[" + users.joinToString(",") {
            "\"${it.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        } + "]"
    }

    @JavascriptInterface
    fun removeIgnoredUser(username: String) {
        val users = repo.getIgnoredUsers().toMutableList()
        users.remove(username)
        repo.setIgnoredUsers(users)
    }

    @JavascriptInterface
    fun getLastUpdatedMs(): Long = repo.getLastUpdated()

    @JavascriptInterface
    fun triggerRefresh() {
        val request = OneTimeWorkRequestBuilder<IgnoreListWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
