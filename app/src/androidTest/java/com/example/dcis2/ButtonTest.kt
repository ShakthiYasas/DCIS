package com.example.dcis2

import com.example.dcis2.testUtility.ButtonStateLogic
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ButtonTest {

    private val buttonStateLogic = ButtonStateLogic()

    @Test
    fun NextButtonEnabledWhenLocationPermissionisGranted() {
        val result = buttonStateLogic.shouldEnableNextButton(true)
        assertTrue(result)
    }

    @Test
    fun NextButtonDisabledWhenLocationPermissionIsDenied() {
        val result = buttonStateLogic.shouldEnableNextButton(false)
        assertFalse(result)
    }
}
