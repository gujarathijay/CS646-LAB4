package com.zybooks.explorerscompass

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.zybooks.explorerscompass.R
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextTask)
        val button = findViewById<Button>(R.id.buttonSetReminder)
        val imageView = findViewById<ImageView>(R.id.imageViewAnimation)

        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        workManager = WorkManager.getInstance(this)

        button.setOnClickListener {
            mediaPlayer.start()
            imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation))

//            val delay = 10L // delay for 10 seconds as an example
            val reminderRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
//                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build()
            workManager.enqueue(reminderRequest)
        }
    }
}

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        showNotification("Reminder", "Don't forget to complete your task!")
        return Result.success()
    }

    private fun showNotification(title: String, text: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_notification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notifications)
            .build()
        notificationManager.notify(1, notification)
    }
}
