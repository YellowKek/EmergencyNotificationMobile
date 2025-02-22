package com.example.emergency.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.emergency.MainViewModel
import com.example.emergency.models.AddGroupRequest
import com.example.emergency.models.User
import com.example.emergency.util.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    user: User,
    mvm: MainViewModel,
    apiService: ApiService,
    emergencyGroup: Map<String, String>,
) {
    var type by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(emergencyGroup) {
        if (emergencyGroup.isNotEmpty()) {
            items.clear()
            items.addAll(emergencyGroup.map { it.key to it.value })
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ShowFields(user = user, mvm = mvm)

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            Text(text = "Emergency group")
            Spacer(modifier = Modifier.height(8.dp))

            EmergencyGroups(
                items = items,
                type = type,
                value = value,
                onTypeChange = { type = it },
                onValueChange = {value = it}
            )

            Spacer(modifier = Modifier.height(16.dp))

            AddGroupButton(
                type = type,
                onTypeChange = { type = it },
                value = value,
                onValueChange = { value = it },
                items = items,
                user = user,
                apiService = apiService
            )
        }
    }
}

@Composable
fun ShowFields(
    user: User,
    mvm: MainViewModel,

    ) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Name: ${user.name}")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row {
        Text(text = "Surname: ${user.surname}")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row {
        Text(text = "Email: ${user.email}")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row {
        Text(text = "Location: ${mvm.getAddressFromLocation()}")
    }
}

@Composable
fun EmergencyGroups(
    items: SnapshotStateList<Pair<String, String>>,
    type: String,
    onTypeChange: (String) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,

    ) {
    LazyColumn {
        items(items) { (itemType, itemValue) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Type: $itemType", modifier = Modifier.weight(1f))
                Text(text = "Value: $itemValue", modifier = Modifier.weight(1f))
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onTypeChange("email") }
            ) {
                RadioButton(
                    selected = type == "email",
                    onClick = { onTypeChange("email") }
                )
                Text(
                    text = "Email",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onTypeChange("sms") }
            ) {
                RadioButton(
                    selected = type == "sms",
                    onClick = { onTypeChange("sms") }
                )
                Text(
                    text = "SMS",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Выбранный тип: $type",
            style = MaterialTheme.typography.bodyMedium
        )
    }



    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = {
            if (type == "email") {
                Text("Введите почту")
            } else {
                Text("Введите номер телефона")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AddGroupButton(
    type: String,
    onTypeChange: (String) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    items: SnapshotStateList<Pair<String, String>>,
    user: User,
    apiService: ApiService,
) {
    Button(
        onClick = {
            if (type.isNotBlank() && value.isNotBlank()) {
                items.add(type to value)

                val body = AddGroupRequest(
                    userId = user.id,
                    group = type,
                    value = value
                )
                apiService.addGroup(body).enqueue(
                    object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("API", "Group added successfully")
                            } else {
                                Log.e("API", "Failed to add group: ${response.body()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("API", "Failed to add group: ${t.message}")
                        }
                    }
                )

                onTypeChange("")
                onValueChange("")
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Add Item")
    }
}