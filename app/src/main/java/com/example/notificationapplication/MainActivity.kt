package com.example.notificationapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    //переменные для уведомления
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotifChannel()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        //создание уведомления
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Уведомление")
            .setContentText("Если пришло, значит работает")
            .setSmallIcon(R.drawable.baseline_info_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notifyManager = NotificationManagerCompat.from(this)

        binding.notifButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                 var requestPermissionLauncher =
                     registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted : Boolean ->
                        if (isGranted) {
                            Log.i("Permission :", "Granted")
                            Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.i("Permission :", "Denied")
                            Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
            notifyManager.notify(NOTIF_ID,notif)
        }
    }

    //создание канала уведомления
    private fun createNotifChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}