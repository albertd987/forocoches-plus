package com.domenechobiol.forocoches

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

    @Test
    fun `getFavoriteUsers devuelve lista vacía inicialmente`() {
        assertTrue(repo.getFavoriteUsers().isEmpty())
    }

    @Test
    fun `addFavoriteUser persiste username y userId`() {
        repo.addFavoriteUser("LuoJi", "882386")
        val favs = repo.getFavoriteUsers()
        assertEquals(1, favs.size)
        assertEquals("882386", favs["LuoJi"])
    }

    @Test
    fun `removeFavoriteUser elimina el usuario`() {
        repo.addFavoriteUser("LuoJi", "882386")
        repo.removeFavoriteUser("LuoJi")
        assertTrue(repo.getFavoriteUsers().isEmpty())
    }

    @Test
    fun `getLastSeenThreadId devuelve null inicialmente`() {
        assertNull(repo.getLastSeenThreadId("LuoJi"))
    }

    @Test
    fun `setLastSeenThreadId persiste y getLastSeenThreadId lo devuelve`() {
        repo.setLastSeenThreadId("LuoJi", "12345")
        assertEquals("12345", repo.getLastSeenThreadId("LuoJi"))
    }

    @Test
    fun `removeFavoriteUser elimina también el lastSeenThreadId`() {
        repo.addFavoriteUser("LuoJi", "882386")
        repo.setLastSeenThreadId("LuoJi", "12345")
        repo.removeFavoriteUser("LuoJi")
        assertNull(repo.getLastSeenThreadId("LuoJi"))
    }
}
