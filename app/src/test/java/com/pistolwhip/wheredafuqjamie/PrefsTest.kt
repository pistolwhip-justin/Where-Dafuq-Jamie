package com.pistolwhip.wheredafuqjamie

import org.junit.Assert.assertEquals
import org.junit.Test

class PrefsTest {
    @Test fun phraseNormalizationContractIsStable() {
        assertEquals("hello world", TriggerLogic.sanitizePhrase("  hello world  "))
        assertEquals("", TriggerLogic.sanitizePhrase(null))
    }
}
