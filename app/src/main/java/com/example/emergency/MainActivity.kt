package com.example.emergency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.emergency.ui.theme.EmergencyTheme
import android.Manifest
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.emergency.models.User
import com.example.emergency.navigation.NavContent
import com.example.emergency.navigation.Page
import com.example.emergency.util.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var requester: ActivityResultLauncher<Array<String>>
    private val mvm: MainViewModel by viewModels<MainViewModel>()
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requester = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                mvm.startLocationUpdates()
            }
        }

        requester.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )

        setContent {
            val navController: NavHostController = rememberNavController()
            EmergencyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    val isAuthenticated = mvm.getAuth()
                    user = mvm.getUser()
                    Log.e("is auth", isAuthenticated.toString())

                    val retrofit = Retrofit.Builder().baseUrl("http://192.168.31.98:8081/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                    val apiService = retrofit.create(ApiService::class.java)


                    LaunchedEffect(isAuthenticated) {
                        if (isAuthenticated) {
                            navController.navigate(Page.MAIN.route) {
                                popUpTo(Page.REGISTRATION.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Page.REGISTRATION.route) {
                                popUpTo(Page.MAIN.route) { inclusive = true }
                            }
                        }
                    }

                    NavContent(
                        navController = navController,
                        mvm = mvm,
                        user = user!!,
                        apiService = apiService
                        )
                }
            }
        }
    }
}
