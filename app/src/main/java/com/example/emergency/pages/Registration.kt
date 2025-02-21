package com.example.emergency.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emergency.models.User

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.emergency.MainViewModel
import com.example.emergency.models.RegisterRequest
import com.example.emergency.navigation.Page
import com.example.emergency.util.ApiService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

@Composable
fun RegistrationScreen(
    navController: NavController,
    user: User,
    mvm: MainViewModel,
    apiService: ApiService
) {
    // Локальное состояние для полей ввода
    var name by remember { mutableStateOf(user.name) }
    var surname by remember { mutableStateOf(user.surname) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf(user.password) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "Registration")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Поле для имени
                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        name = newName
                        user.name = newName
                    },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для фамилии
                OutlinedTextField(
                    value = surname,
                    onValueChange = { newSurname ->
                        surname = newSurname
                        user.surname = newSurname
                    },
                    label = { Text("Surname") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для email
                OutlinedTextField(
                    value = email,
                    onValueChange = { newEmail ->
                        email = newEmail
                        user.email = newEmail
                    },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { newPassword ->
                        password = newPassword
                        user.password = newPassword
                    },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val gson = Gson()
                        val body = RegisterRequest(
                            email = user.email,
                            name = user.name,
                            surname = user.surname,
                            password = user.password
                        )

                        apiService.createUser(body).enqueue(
                            object : Callback<ResponseBody> {
                                override fun onResponse(
                                    call: Call<ResponseBody>,
                                    response: retrofit2.Response<ResponseBody>
                                ) {
                                    if (response.isSuccessful) {
                                        val jsonString = response.body()?.string()

                                        if (!jsonString.isNullOrEmpty()) {
                                            try {
                                                val userJSON: User =
                                                    gson.fromJson(jsonString, User::class.java)
                                                user.id = userJSON.id
                                                user.name = userJSON.name
                                                user.surname = userJSON.surname
                                                user.email = userJSON.email
                                                user.password = userJSON.password

                                                Log.e("api register", user.toString())
                                                mvm.authenticate(user)
                                                navController.navigate(Page.MAIN.route)
                                            } catch (e: JsonSyntaxException) {
                                                Log.e(
                                                    "api register",
                                                    "Ошибка разбора JSON: ${e.message}"
                                                )
                                            }
                                        } else {
                                            Log.e("api register", "Response body is empty")
                                        }
                                    } else {
                                        Log.e(
                                            "api register",
                                            "Request failed with code: ${response.code()} ${response.body()}"
                                        )
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.e(
                                        "api register",
                                        call.toString() + " " + t.message.toString()
                                    )
                                }
                            })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Blue)
                ) {
                    Text(text = "Submit")
                }

                Spacer(modifier = Modifier.height(32.dp))

            }
        }
    )
}