package com.example.emergency.locating

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object Locator: LocationCallback() {
    val locationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            900000,
        ).setMinUpdateDistanceMeters(5f)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    private val _location: MutableStateFlow<Location?> =
        MutableStateFlow(null)

    val location: StateFlow<Location?>
        get() = _location

    override fun onLocationResult(locationResult: LocationResult) {
        for (loc in locationResult.locations) {
            CoroutineScope(Dispatchers.IO).launch {
                _location.emit(loc)
            }
        }
    }
}