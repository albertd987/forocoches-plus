package com.domenechobiol.forocoches

import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.runBlocking

class SettingsBridge(
    private val repo: IgnoreListRepository,
    private val notifRepo: NotificationRepository,
    private val webView: WebView
) {

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
        Thread {
            val cookie = CookieManager.getInstance().getCookie("https://forocoches.com")
                ?: run { notifyRefreshDone(); return@Thread }
            if (cookie.isBlank()) { notifyRefreshDone(); return@Thread }
            try {
                val users = runBlocking { IgnoreListFetcher().fetch(cookie) }
                if (users.isNotEmpty()) repo.setIgnoredUsers(users)
            } catch (_: Exception) { }
            notifyRefreshDone()
        }.start()
    }

    @JavascriptInterface
    fun getFavoriteUsersJson(): String {
        val favs = notifRepo.getFavoriteUsers()
        if (favs.isEmpty()) return "[]"
        return "[" + favs.entries.joinToString(",") {
            "{\"username\":\"${escapeJson(it.key)}\",\"userId\":\"${escapeJson(it.value)}\"}"
        } + "]"
    }

    @JavascriptInterface
    fun addFavoriteUser(username: String, userId: String) {
        notifRepo.addFavoriteUser(username, userId)
    }

    @JavascriptInterface
    fun removeFavoriteUser(username: String) {
        notifRepo.removeFavoriteUser(username)
    }

    private fun notifyRefreshDone() {
        webView.post {
            webView.evaluateJavascript("if(window.fcOnRefreshDone)window.fcOnRefreshDone()", null)
        }
    }

    private fun escapeJson(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
}
