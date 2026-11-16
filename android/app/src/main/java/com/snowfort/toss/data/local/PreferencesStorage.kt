package com.snowfort.toss.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun wipeProgress() {
        prefs.edit()
            .remove(KEY_STARS)
            .remove(KEY_CURRENT_YARD)
            .remove(KEY_YARDS_CLEARED)
            .remove(KEY_BEST_STARS)
            .remove(KEY_BEST_ACCURACY)
            .remove(KEY_FORT_LAYOUT)
            .remove(KEY_OWNED_MITTENS)
            .remove(KEY_EQUIPPED_MITTEN)
            .apply()
    }

    companion object {
        const val STORE_NAME = "snowfort_toss_store"
        const val KEY_STARS = "stars"
        const val KEY_CURRENT_YARD = "current_yard"
        const val KEY_YARDS_CLEARED = "yards_cleared"
        const val KEY_BEST_STARS = "best_stars"
        const val KEY_BEST_ACCURACY = "best_accuracy"
        const val KEY_FORT_LAYOUT = "fort_layout"
        const val KEY_OWNED_MITTENS = "owned_mittens"
        const val KEY_EQUIPPED_MITTEN = "equipped_mitten"
        const val KEY_SOUND = "sound"
        const val KEY_HAPTICS = "haptics"
        const val KEY_ASSIST = "arc_assist"
    }
}
