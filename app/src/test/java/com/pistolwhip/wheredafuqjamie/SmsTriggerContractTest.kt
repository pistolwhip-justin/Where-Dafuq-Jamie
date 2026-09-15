package com.pistolwhip.wheredafuqjamie

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsTriggerContractTest {
    @Test fun exactActivationIsAccepted() {
        assertTrue(TriggerLogic.shouldActivate("Find Jamie", "Find Jamie", "Stop Jamie"))
    }

    @Test fun deactivationPhraseCannotAlsoActivate() {
        assertFalse(TriggerLogic.shouldActivate("Stop Jamie", "Stop Jamie", "Stop Jamie"))
    }

    @Test fun messageContainingPhraseIsRejected() {
        assertFalse(TriggerLogic.shouldActivate("please Find Jamie", "Find Jamie", "Stop Jamie"))
    }
}
