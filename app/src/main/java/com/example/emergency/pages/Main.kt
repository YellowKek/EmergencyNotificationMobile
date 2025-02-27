package com.example.emergency.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergency.MainViewModel
import com.example.emergency.models.ApiError
import com.example.emergency.models.EmergencyRequest
import com.example.emergency.models.User
import com.example.emergency.navigation.Page
import com.example.emergency.util.ApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Main(
    modifier: Modifier = Modifier,
    navController: NavController,
    apiService: ApiService,
    user: User,
    mvm: MainViewModel
) {
    Scaffold(modifier = modifier, topBar = {
        Header()
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopEnd
        ) {
            ProfileButton(onClick = { navController.navigate(Page.PROFILE.route) })
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            HelpButton(
                user = user,
                mvm = mvm,
                apiService = apiService
            )
        }
    }
}

@Composable
fun ProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .size(70.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)

    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile Icon",
            tint = Color.White,
        )
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 30.dp, 0.dp, 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Service for notifying",
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
        )
    }
}

@Composable
fun HelpButton(
    user: User,
    mvm: MainViewModel,
    apiService: ApiService,

    ) {
    val context = LocalContext.current
    Button(
        modifier = Modifier
            .size(300.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        onClick = {
            mvm.updateLocation { location ->
                if (location != null) {
                    val body = EmergencyRequest(
                        userId = user.id,
                        location = mvm.getAddressFromLocation()!!
                    )
                    Log.e("debug", user.toString())
                    Log.e("api help", "userID " + body.userId.toString())

                    apiService.callEmergency(body).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            val gson = Gson()
                            val apiError = gson.fromJson(response.body()?.string(), ApiError::class.java)
                            Toast.makeText(context, apiError.message, Toast.LENGTH_SHORT).show()
                            Log.e("api help", "response " + response.body().toString())
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                            Log.e("api help", "failure $t")
                        }
                    })
                } else {
                    Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                    Log.e("api help", "Failed to get location")
                }
            }
        }
    ) {
        Text(
            text = "HELP",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}