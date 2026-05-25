package com.fcplus.forocoches

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationFetcherTest {

    private val fetcher = NotificationFetcher()

    @Test
    fun `parsePmCount devuelve 0 si no hay PMs en el HTML`() {
        assertEquals(0, fetcher.parsePmCount("<html><body></body></html>"))
    }

    @Test
    fun `parsePmCount extrae el número de mensajes privados`() {
        val html = """<a href="private.php">Mensajes Privados <span class="badge-count">5</span></a>"""
        assertEquals(5, fetcher.parsePmCount(html))
    }

    @Test
    fun `parsePmCount extrae desde formato texto parenthesis`() {
        val html = """<a href="private.php">Mensajes Privados (3 nuevos)</a>"""
        assertEquals(3, fetcher.parsePmCount(html))
    }

    @Test
    fun `parseNotifCount devuelve 0 si no hay notificaciones`() {
        assertEquals(0, fetcher.parseNotifCount("<html><body></body></html>"))
    }

    @Test
    fun `parseNotifCount extrae el contador de notificaciones`() {
        val html = """<span class="notification-count">12</span>"""
        assertEquals(12, fetcher.parseNotifCount(html))
    }

}
