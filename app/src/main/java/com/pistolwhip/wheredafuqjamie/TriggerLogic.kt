package com.pistolwhip.wheredafuqjamie

/** Pure trigger/deactivation rules so they can be exhaustively unit-tested on the JVM. */
object TriggerLogic {
    fun matchesPhrase(message: String?, configuredPhrase: String?): Boolean {
        val phrase = configuredPhrase?.trim().orEmpty()
        val text = message?.trim().orEmpty()
        return phrase.isNotEmpty() && text.equals(phrase, ignoreCase = true)
    }

    fun shouldActivate(message: String?, activatePhrase: String?, deactivatePhrase: String?): Boolean {
        if (matchesPhrase(message, deactivatePhrase)) return false
        return matchesPhrase(message, activatePhrase)
    }

    fun shouldDeactivate(message: String?, deactivatePhrase: String?): Boolean =
        matchesPhrase(message, deactivatePhrase)

    fun sanitizePhrase(value: String?): String = value?.trim().orEmpty()

    fun repeatIntervalMs(seconds: Int): Long = seconds.coerceAtLeast(1).toLong() * 1000L
}
