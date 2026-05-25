package com.fcplus.forocoches

import android.content.Context

class KeywordRepository(context: Context) {

    private val prefs = context.getSharedPreferences("fc_keywords", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_KEYWORDS = setOf(
            "PP", "PSOE", "VOX", "Podemos", "Sumar", "Ciudadanos",
            "política", "político", "políticos", "elecciones", "gobierno",
            "Congreso", "Senado", "Sánchez", "Feijóo", "Abascal",
            "independencia", "Cataluña", "separatismo"
        )
    }

    fun isEnabled(): Boolean = prefs.getBoolean("enabled", true)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("enabled", enabled).apply()
    }

    fun getKeywords(): Set<String> {
        if (!prefs.contains("keywords")) return DEFAULT_KEYWORDS
        return prefs.getStringSet("keywords", DEFAULT_KEYWORDS) ?: DEFAULT_KEYWORDS
    }

    fun addKeyword(keyword: String) {
        val current = getKeywords().toMutableSet()
        current.add(keyword)
        prefs.edit().putStringSet("keywords", current).apply()
    }

    fun removeKeyword(keyword: String) {
        val current = getKeywords().toMutableSet()
        current.remove(keyword)
        prefs.edit().putStringSet("keywords", current).apply()
    }

    fun resetToDefaults() {
        prefs.edit().remove("keywords").apply()
    }
}
