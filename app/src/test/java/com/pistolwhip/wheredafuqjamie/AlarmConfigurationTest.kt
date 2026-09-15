package com.pistolwhip.wheredafuqjamie

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmConfigurationTest {
    @Test fun defaultRepeatIntervalIsThreeSeconds() {
        assertEquals(3000L, TriggerLogic.repeatIntervalMs(3))
    }

    @Test fun minimumRepeatIntervalPreventsZeroOrNegativeDelay() {
        assertTrue(TriggerLogic.repeatIntervalMs(0) >= 1000L)
        assertTrue(TriggerLogic.repeatIntervalMs(-10) >= 1000L)
    }

    @Test fun phraseStorageBoundaryIsNormalized() {
        assertEquals("activate me", TriggerLogic.sanitizePhrase("  activate me  "))
    }
}
