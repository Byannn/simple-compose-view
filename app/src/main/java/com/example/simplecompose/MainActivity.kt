package com.example.simplecompose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.simplecompose.ui.theme.SimpleComposeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            SimpleComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen() // Menampilkan UI utama
                }
            }
        }
    }

    private fun getScheduleFromDatabase(onResult: (List<Schedule>) -> Unit) {
        val database = Firebase.database
        val myRef = database.getReference("jadwalKuliah")

        myRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val schedules = snapshot.children.mapNotNull { scheduleSnapshot ->
                    scheduleSnapshot.getValue(Schedule::class.java)
                }
                onResult(schedules)
            } else {
                onResult(emptyList()) // Jika tidak ada jadwal
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Gagal mengambil data", it)
            onResult(emptyList()) // Kembali dengan jadwal kosong jika gagal
        }
    }

    @Composable
    fun MainScreen() {
        var name by remember { mutableStateOf("") }
        var nim by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val isFormValid = name.isNotBlank() && nim.isNotBlank()
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // TextField untuk Nama
            InputFieldWithIcon(
                value = name,
                onValueChange = { name = it },
                label = "Masukkan Nama",
                icon = Icons.Default.AccountBox,
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // TextField untuk NIM
            InputFieldWithIcon(
                value = nim,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) nim = it
                },
                label = "Masukkan NIM",
                icon = Icons.Default.AccountBox,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        errorMessage = null // Reset error message
                        if (isFormValid) {
                            if (name == "Abyan Rifqi Zainum Muttaqin" && nim == "225150201111033") {
                                // Mengambil jadwal dari database
                                getScheduleFromDatabase { schedules ->
                                    Toast.makeText(
                                        context,
                                        "Login sukses!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navigateToListActivity(context, schedules) // Kirim jadwal ke ListActivity
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nama atau NIM tidak valid!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .width(110.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Submit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        name = ""
                        nim = ""
                        errorMessage = null // Reset error message
                    },
                    modifier = Modifier.width(110.dp)
                ) {
                    Text("Reset")
                }
            }

            // Menampilkan pesan error jika ada
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    @Composable
    fun InputFieldWithIcon(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        icon: ImageVector,
        modifier: Modifier = Modifier,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = keyboardOptions
            )
        }
    }

    private fun navigateToListActivity(context: Context, schedules: List<Schedule>) {
        val intent = Intent(context, ListActivity::class.java).apply {
            putExtra("schedules", ArrayList(schedules)) // Mengirim jadwal ke ListActivity
        }
        context.startActivity(intent)
    }
}