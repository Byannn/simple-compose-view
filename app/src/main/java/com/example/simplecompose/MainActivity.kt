package com.example.simplecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplecompose.ui.theme.BlueGrey
import com.example.simplecompose.ui.theme.DarkerGreen
import com.example.simplecompose.ui.theme.SimpleComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen() // Menampilkan UI utama
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen() {
    // Menyimpan nilai input nama dan NIM
    var nameInput by remember { mutableStateOf("") }
    var nimInput by remember { mutableStateOf("") }
    var displayText by remember { mutableStateOf("") }

    // Cek apakah form sudah terisi lengkap
    val isFormValid = nameInput.isNotBlank() && nimInput.isNotBlank()

    // Menampilkan layout form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Menampilkan teks hasil input (Nama + NIM)
        Text(text = displayText)
        Spacer(modifier = Modifier.height(16.dp))

        // Input untuk Nama
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Masukkan nama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Input untuk NIM (hanya bisa memasukkan angka)
        OutlinedTextField(
            value = nimInput,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    nimInput = it
                }
            },
            label = { Text("Masukkan NIM") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Baris tombol Submit dan Reset
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Tombol Submit
            Button(
                onClick = {
                    if (isFormValid) {
                        displayText = "$nameInput\n $nimInput"
                    }
                },
                modifier = Modifier
                    .width(110.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) DarkerGreen else Color.LightGray
                ),
                enabled = isFormValid
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Tombol Reset
            Button(
                onClick = {
                    nameInput = ""
                    nimInput = ""
                    displayText = ""
                },
                modifier = Modifier.width(110.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueGrey)
            ) {
                Text("Reset")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimpleComposeTheme {
        MyScreen() // Preview dari layout MyScreen
    }
}