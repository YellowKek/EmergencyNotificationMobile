package com.example.emergency

import android.app.Application
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.emergency.locating.Locator
import com.example.emergency.models.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val applicationContext = application.applicationContext
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)
    private lateinit var _location: Location

    private fun getAuthFromFile(): Boolean {
        return try {
            val file = File(applicationContext.filesDir, "isAuth.txt")
            if (file.exists()) {
                file.readText().toBoolean()
            } else {
                file.writeText("false")
                false
            }
        } catch (e: Exception) {
            Log.e("getAuthFromFile", "Error reading auth file", e)
            false
        }
    }

    private var isAuthenticated = getAuthFromFile()
    fun authenticate(user: User) {
        isAuthenticated = true
        val file = File(applicationContext.filesDir, "isAuth.txt")
        file.writeText("true")
        val gson = Gson()
        val json = gson.toJson(user)
        val userFile = File(applicationContext.filesDir, "user.json")
        userFile.writeText(json)
    }

    fun getUser(): User {
        val userFile = File(applicationContext.filesDir, "user.json")
        return if (userFile.exists()) {
            val json = userFile.readText()
            val gson = Gson()
            gson.fromJson(json, User::class.java)
        } else {
            User()
        }
    }


    fun getAuth(): Boolean {
        val file = File(applicationContext.filesDir, "isAuth.txt")
        Log.e("from file", file.readText())
        return getAuthFromFile()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.location.collect { loc ->
                withContext(Dispatchers.Main) {
                    loc?.let { _location = it }
                }
            }
        }
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener {
            viewModelScope.launch {
                fusedLocationClient.requestLocationUpdates(
                    Locator.locationRequest,
                    Locator,
                    Looper.getMainLooper()
                )
            }
        }
    }

    fun updateLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback(null)
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                _location = task.result
                callback(_location)
            } else {
                callback(null)
            }
        }
    }

    fun getAddressFromLocation(): String? {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(_location.latitude, _location.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val addressText = StringBuilder().apply {
                    for (i in 0..address.maxAddressLineIndex) {
                        append(address.getAddressLine(i)).append(", ")
                    }
                }.toString()
                addressText.removeSuffix(", ")
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}