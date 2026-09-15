package com.pistolwhip.wheredafuqjamie

import android.content.Context
import android.net.Uri

object Prefs {
    private const val FILE = "jamie_prefs"
    private const val ACTIVATE = "activate_phrase"
    private const val DEACTIVATE = "deactivate_phrase"
    private const val ALARM_TYPE = "alarm_type"
    private const val ALARM_URI = "alarm_uri"
    private const val VOICE = "voice_text"
    private const val BG = "bg_color"
    private const val POPUP = "popup_color"
    private const val ALPHA = "alpha"
    private const val LOG_URI = "log_uri"

    private fun p(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    fun activate(c: Context) = p(c).getString(ACTIVATE, "Where Dafuq Jamie") ?: "Where Dafuq Jamie"
    fun deactivate(c: Context) = p(c).getString(DEACTIVATE, "Jamie I'm here") ?: "Jamie I'm here"
    fun alarmType(c: Context) = p(c).getString(ALARM_TYPE, "tone") ?: "tone"
    fun alarmUri(c: Context): Uri? = p(c).getString(ALARM_URI, null)?.let(Uri::parse)
    fun voice(c: Context) = p(c).getString(VOICE, "Jamie, where the hell are you?") ?: "Jamie, where the hell are you?"
    fun bg(c: Context) = p(c).getInt(BG, 0xFF101010.toInt())
    fun popup(c: Context) = p(c).getInt(POPUP, 0xFF2E7D32.toInt())
    fun alpha(c: Context) = p(c).getInt(ALPHA, 100)
    fun logUri(c: Context): Uri? = p(c).getString(LOG_URI, null)?.let(Uri::parse)

    fun save(c: Context, activate: String, deactivate: String, alarmType: String, alarmUri: Uri?, voice: String, bg: Int, popup: Int, alpha: Int, logUri: Uri?) {
        p(c).edit()
            .putString(ACTIVATE, activate.trim())
            .putString(DEACTIVATE, deactivate.trim())
            .putString(ALARM_TYPE, alarmType)
            .putString(ALARM_URI, alarmUri?.toString())
            .putString(VOICE, voice.trim())
            .putInt(BG, bg)
            .putInt(POPUP, popup)
            .putInt(ALPHA, alpha.coerceIn(10, 100))
            .putString(LOG_URI, logUri?.toString())
            .apply()
    }
}
