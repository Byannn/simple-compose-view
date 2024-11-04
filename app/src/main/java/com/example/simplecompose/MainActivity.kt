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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.simplecompose.ui.theme.SimpleComposeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.input.VisualTransformation

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
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val isFormValid = email.isNotBlank() && password.isNotBlank()
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // TextField for Email
            InputFieldWithIcon(
                value = email,
                onValueChange = { email = it },
                label = "Enter Email",
                icon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // TextField for Password
            InputFieldWithIcon(
                value = password,
                onValueChange = { password = it },
                label = "Enter Password",
                icon = Icons.Default.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        errorMessage = null // Reset error message
                        if (isFormValid) {
                            signInWithEmailAndPassword(context, email, password)
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .width(110.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        email = ""
                        password = ""
                        errorMessage = null // Reset error message
                    },
                    modifier = Modifier.width(110.dp)
                ) {
                    Text("Reset")
                }
            }

            // Show error message
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

    private fun signInWithEmailAndPassword(context: Context, email: String, password: String) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Fetch schedules from database and navigate to ListActivity
                    getScheduleFromDatabase { schedules ->
                        navigateToListActivity(context, schedules)
                    }
                } else {
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None
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
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation
            )
        }
    }

    private fun navigateToListActivity(context: Context, schedules: List<Schedule>) {
        val intent = Intent(context, ListActivity::class.java).apply {
            putExtra("schedules", ArrayList(schedules)) // Send schedules to ListActivity
        }
        context.startActivity(intent)
    }
}