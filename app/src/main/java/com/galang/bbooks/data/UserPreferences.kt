package com.galang.bbooks.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bbooks_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, true) // Default to true (Dark Theme)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }
}
