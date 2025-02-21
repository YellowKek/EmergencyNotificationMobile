package com.example.emergency.util

import com.example.emergency.models.EmergencyRequest
import com.example.emergency.models.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    fun createUser(@Body body: RegisterRequest): Call<ResponseBody>

    @POST("emergency")
    fun callEmergency(@Body body: EmergencyRequest): Call<ResponseBody>
}