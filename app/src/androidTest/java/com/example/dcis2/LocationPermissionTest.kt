//package com.example.dcis2
//import android.content.pm.PackageManager
//import androidx.test.core.app.ApplicationProvider
//import com.example.dcis2.ultility.LocationUtils
//import org.mockito.Mockito
//import org.junit.Assert.assertFalse
//import org.junit.Test
//
//class LocationPermissionTest {
//
//    @Test
//    fun ReturnFalseWhenLocationPermissionIsDenied() {
//        val context = Mockito.mock(ApplicationProvider.getApplicationContext()::class.java)
//        Mockito.`when`(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        ).thenReturn(PackageManager.PERMISSION_DENIED)
//
//        val result = LocationUtils.isLocationPermissionGranted(context)
//        assertFalse(result)
//    }
//}
