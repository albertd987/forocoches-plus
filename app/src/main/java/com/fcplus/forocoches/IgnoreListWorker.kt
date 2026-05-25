package com.fcplus.forocoches

import android.content.Context
import android.webkit.CookieManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class IgnoreListWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cookie = CookieManager.getInstance().getCookie("https://forocoches.com")
            ?: return Result.success()
        if (cookie.isBlank()) return Result.success()

        return try {
            val users = IgnoreListFetcher().fetch(cookie)
            if (users.isNotEmpty()) {
                IgnoreListRepository(applicationContext).setIgnoredUsers(users)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
