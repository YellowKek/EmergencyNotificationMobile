package com.example.emergency.models

import com.google.gson.annotations.SerializedName

data class AddGroupRequest (
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("group")
    var group: String,
    @SerializedName("value")
    var value: String
)