package com.pistolwhip.wheredafuqjamie

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerLogicTest {
    @Test fun exactPhraseMatches() {
        assertTrue(TriggerLogic.matchesPhrase("Where Dafuq Jamie", "Where Dafuq Jamie"))
    }

    @Test fun matchingIsCaseInsensitive() {
        assertTrue(TriggerLogic.matchesPhrase("WHERE DAFUQ JAMIE", "where dafuq jamie"))
    }

    @Test fun surroundingWhitespaceDoesNotMatter() {
        assertTrue(TriggerLogic.matchesPhrase("  where dafuq jamie  ", " where dafuq jamie "))
    }

    @Test fun partialPhraseDoesNotActivate() {
        assertFalse(TriggerLogic.matchesPhrase("where dafuq", "where dafuq jamie"))
        assertFalse(TriggerLogic.matchesPhrase("where dafuq jamie now", "where dafuq jamie"))
    }

    @Test fun blankConfiguredPhraseNeverMatches() {
        assertFalse(TriggerLogic.matchesPhrase("anything", ""))
        assertFalse(TriggerLogic.matchesPhrase("anything", "   "))
        assertFalse(TriggerLogic.matchesPhrase("anything", null))
    }

    @Test fun nullMessageNeverMatches() {
        assertFalse(TriggerLogic.matchesPhrase(null, "trigger"))
    }

    @Test fun deactivationWinsIfBothPhrasesAreIdentical() {
        assertFalse(TriggerLogic.shouldActivate("panic", "panic", "panic"))
    }

    @Test fun activationRequiresActivationPhrase() {
        assertFalse(TriggerLogic.shouldActivate("other", "activate", "deactivate"))
    }

    @Test fun deactivationMatchesOnlyConfiguredPhrase() {
        assertTrue(TriggerLogic.shouldDeactivate("STOP", "stop"))
        assertFalse(TriggerLogic.shouldDeactivate("stop now", "stop"))
    }

    @Test fun sanitizePhraseHandlesNullAndWhitespace() {
        assertEquals("hello", TriggerLogic.sanitizePhrase("  hello  "))
        assertEquals("", TriggerLogic.sanitizePhrase(null))
        assertEquals("", TriggerLogic.sanitizePhrase("   "))
    }

    @Test fun repeatIntervalUsesMilliseconds() {
        assertEquals(3000L, TriggerLogic.repeatIntervalMs(3))
        assertEquals(1000L, TriggerLogic.repeatIntervalMs(0))
        assertEquals(1000L, TriggerLogic.repeatIntervalMs(-5))
    }
}
