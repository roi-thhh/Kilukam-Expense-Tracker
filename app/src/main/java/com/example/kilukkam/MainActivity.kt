package com.example.kilukkam

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.theme.KilukkamTheme
import com.example.kilukkam.ui.main.MainScreen

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
      // Handle permission responses if needed
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    createNotificationChannel()
    requestPermissionsIfNeeded()
    
    val initialAmount = intent.getDoubleExtra("transaction_amount", -1.0).takeIf { it > 0 }
    val showDialog = intent.getBooleanExtra("show_categorize_dialog", false)

    val repository = DataRepository(applicationContext)

    enableEdgeToEdge()
    setContent {
      KilukkamTheme { 
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
           var showSplash by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
           
           if (showSplash) {
               com.example.kilukkam.ui.SplashScreen(onSplashFinished = { showSplash = false })
           } else {
               com.example.kilukkam.ui.main.MainAppScaffold(
                   repository = repository,
                   initialAmount = initialAmount,
                   showCategorizeDialog = showDialog
               ) 
           }
        } 
      }
    }
  }

  private fun createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          val name = "Transaction Alerts"
          val descriptionText = "Notifications for new transactions to categorize"
          val importance = NotificationManager.IMPORTANCE_HIGH
          val channel = NotificationChannel("TRANSACTION_CHANNEL", name, importance).apply {
              description = descriptionText
          }
          val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          notificationManager.createNotificationChannel(channel)
      }
  }

  private fun requestPermissionsIfNeeded() {
      val permissionsToRequest = mutableListOf<String>()
      
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
          permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
      }
      
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
          permissionsToRequest.add(Manifest.permission.READ_SMS)
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
              permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
          }
      }

      if (permissionsToRequest.isNotEmpty()) {
          requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
      }
  }
}
