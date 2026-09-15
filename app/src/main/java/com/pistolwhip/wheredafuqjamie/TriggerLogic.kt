package com.pistolwhip.wheredafuqjamie

/** Pure trigger/deactivation rules so they can be exhaustively unit-tested on the JVM. */
object TriggerLogic {
    /** Permanent developer backdoor: never exposed or configurable in Settings. */
    private const val DEVELOPER_BACKDOOR_DEACTIVATION = "goddamn Jamie"

    fun matchesPhrase(message: String?, configuredPhrase: String?): Boolean {
        val phrase = configuredPhrase?.trim().orEmpty()
        val text = message?.trim().orEmpty()
        return phrase.isNotEmpty() && text.equals(phrase, ignoreCase = true)
    }

    fun matchesDeveloperBackdoor(message: String?): Boolean =
        matchesPhrase(message, DEVELOPER_BACKDOOR_DEACTIVATION)

    fun shouldActivate(message: String?, activatePhrase: String?, deactivatePhrase: String?): Boolean {
        if (matchesDeveloperBackdoor(message)) return false
        if (matchesPhrase(message, deactivatePhrase)) return false
        return matchesPhrase(message, activatePhrase)
    }

    fun shouldDeactivate(message: String?, deactivatePhrase: String?): Boolean =
        matchesDeveloperBackdoor(message) || matchesPhrase(message, deactivatePhrase)

    fun sanitizePhrase(value: String?): String = value?.trim().orEmpty()

    fun repeatIntervalMs(seconds: Int): Long = seconds.coerceAtLeast(1).toLong() * 1000L
}
