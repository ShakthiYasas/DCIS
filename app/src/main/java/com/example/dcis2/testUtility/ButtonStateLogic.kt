package com.example.dcis2.testUtility

class ButtonStateLogic {
    fun shouldEnableNextButton(isLocationPermissionGranted: Boolean): Boolean {
        return isLocationPermissionGranted
    }
}