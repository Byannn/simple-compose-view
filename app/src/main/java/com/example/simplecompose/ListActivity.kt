package com.example.simplecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.simplecompose.ui.theme.SimpleComposeTheme

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListScreen() // Menampilkan UI List
                }
            }
        }
    }
}

@Composable
fun ListScreen() {
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Gunakan reference ke child spesifik 'jadwalKuliah' di Firebase Realtime Database
    val database: DatabaseReference = Firebase.database.reference.child("jadwalkuliah")

    // Mengambil data dari Firebase Realtime Database
    LaunchedEffect(Unit) {
        database.get()
            .addOnSuccessListener { snapshot ->
                schedules = snapshot.children.mapNotNull { it.getValue(Schedule::class.java) }
                loading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error getting data: ${exception.message}"
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Jadwal Kuliah",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            schedules.isEmpty() -> {
                Text(text = "Tidak ada data.")
            }
            else -> {
                LazyColumn {
                    items(schedules) { schedule ->
                        ScheduleCard(schedule) // Menampilkan data jadwal kuliah
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Tindakan saat card diklik
                // Bisa digunakan untuk menavigasi ke detail jadwal atau menampilkan pesan toast
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Hari: ${schedule.hari}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Mata Kuliah: ${schedule.matakuliah}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Kode Mata Kuliah: ${schedule.kode}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Kelas: ${schedule.kelas}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Dosen: ${schedule.dosen}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Jam: ${schedule.jam}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ruang: ${schedule.ruang}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}