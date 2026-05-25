package com.fcplus.forocoches

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class NotificationFetcher {

    companion object {
        private const val BASE_URL = "https://forocoches.com/foro"
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

        // FC usa <span class="user-notifications">N</span>: primero PMs, segundo menciones
        private val COUNT_SPAN = Regex("""<span class="user-notifications">(\d+)</span>""", RegexOption.IGNORE_CASE)

    }

    suspend fun fetchMainPage(cookie: String): String = withContext(Dispatchers.IO) {
        get("$BASE_URL/", cookie)
    }

    fun parseAllCounts(html: String): Pair<Int, Int> {
        val matches = COUNT_SPAN.findAll(html).toList()
        val pm = matches.getOrNull(0)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val notif = matches.getOrNull(1)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        return Pair(pm, notif)
    }

    fun parsePmCount(html: String): Int = parseAllCounts(html).first

    fun parseNotifCount(html: String): Int = parseAllCounts(html).second

    private fun get(url: String, cookie: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.setRequestProperty("Cookie", cookie)
        conn.setRequestProperty("User-Agent", USER_AGENT)
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000
        val code = conn.responseCode
        if (code !in 200..299) throw java.io.IOException("HTTP $code for $url")
        return try {
            conn.inputStream.bufferedReader().readText()
        } finally {
            conn.disconnect()
        }
    }
}
