package com.example.emergency.models

import com.google.gson.annotations.SerializedName

data class EmergencyRequest (
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("location")
    var location: String,
)