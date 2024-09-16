package com.example.simplecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplecompose.ui.theme.BlueGrey
import com.example.simplecompose.ui.theme.DarkerGreen
import com.example.simplecompose.ui.theme.SimpleComposeTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))

        // TextField untuk nama
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter name") },
            modifier = Modifier
                .width(240.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // TextField untuk NIM
        TextField(
            value = nim,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) { // Memastikan hanya angka yang bisa diinput
                    nim = it
                }
            },
            label = { Text("Enter NIM") },
            modifier = Modifier
                .width(240.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Menampilkan keyboard numerik
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { text = "Name: $inputText\nNIM: $nim" },
                modifier = Modifier
                    .width(100.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkerGreen
                )
            ) {
                Text("Submit")
            }
            Button(
                onClick = {
                    inputText = ""
                    nim = ""
                    text = ""
                },
                modifier = Modifier
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueGrey
                )
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
        MyScreen()
    }
}