package com.example.emergency.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emergency.MainViewModel
import com.example.emergency.models.User

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    user: User?,
    mvm: MainViewModel
) {
    Log.e("user", user.toString())
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                if (user != null) {
                    Text(text = "Name: ${user.name}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                if (user != null) {
                    Text(text = "Surname: ${user.surname}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                if (user != null) {
                    Text(text = "Email: ${user.email}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(text = "${mvm._location.latitude} ${mvm._location.longitude}")
            }
        }
    }
}