package com.example.emergency.models

import com.google.gson.annotations.SerializedName


class User {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("email")
    var email: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("surname")
    var surname: String = ""

    var password: String = ""

    override fun toString(): String {
        return "User(id=$id, email='$email', name='$name', surname='$surname', password='$password')"
    }

}
