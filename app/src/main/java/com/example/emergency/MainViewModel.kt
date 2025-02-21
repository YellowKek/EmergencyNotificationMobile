package com.example.emergency

import android.app.Application
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
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

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val applicationContext = application.applicationContext
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)
    lateinit var _location: Location

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
        var userFile = File(applicationContext.filesDir, "user.json")
        userFile.writeText(json)
    }

    fun getUser(): User? {
        val userFile = File(applicationContext.filesDir, "user.json")
        return if (userFile.exists()) {
            val json = userFile.readText()
            val gson = Gson()
            gson.fromJson(json, User::class.java)
        } else {
            null
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

//    private suspend fun createUser() {
//
//    }
}