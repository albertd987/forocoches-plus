package com.fcplus.forocoches

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationRepositoryTest {

    private lateinit var repo: NotificationRepository

    @Before
    fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        repo = NotificationRepository(ctx)
    }

    @Test
    fun `getLastPmCount devuelve -1 inicialmente`() {
        assertEquals(-1, repo.getLastPmCount())
    }

    @Test
    fun `setLastPmCount persiste el valor`() {
        repo.setLastPmCount(3)
        assertEquals(3, repo.getLastPmCount())
    }

    @Test
    fun `getLastNotifCount devuelve -1 inicialmente`() {
        assertEquals(-1, repo.getLastNotifCount())
    }

    @Test
    fun `setLastNotifCount persiste el valor`() {
        repo.setLastNotifCount(7)
        assertEquals(7, repo.getLastNotifCount())
    }

}
