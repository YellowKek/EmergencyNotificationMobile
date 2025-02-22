package com.example.emergency.util

import com.example.emergency.models.AddGroupRequest
import com.example.emergency.models.EmergencyRequest
import com.example.emergency.models.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    fun createUser(@Body body: RegisterRequest): Call<ResponseBody>

    @POST("emergency")
    fun callEmergency(@Body body: EmergencyRequest): Call<ResponseBody>

    @POST("addGroup")
    fun addGroup(@Body body: AddGroupRequest): Call<ResponseBody>

    @GET("getGroups")
    fun getGroups(@Query("user_id") userId: Int): Call<ResponseBody>
}