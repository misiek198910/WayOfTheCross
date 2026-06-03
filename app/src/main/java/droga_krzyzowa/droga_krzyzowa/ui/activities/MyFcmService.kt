package droga_krzyzowa.droga_krzyzowa.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.net.toUri
import android.R
import android.content.Context
import androidx.core.content.edit

class MyFcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Logujemy wszystkie dane dodatkowe, które przyszły
        Log.d("FCM_TEST", "Dane: ${remoteMessage.data}")


        // Sprawdzamy, czy w danych powiadomienia przesłaliśmy klucz promujący Moją Parafię [cite: 2026-02-17]
        val isPromo = remoteMessage.data["action"] == "promote_parish"
        Log.d("FCM_TEST", "Czy to promo? $isPromo")

        remoteMessage.notification?.let {
            showNotification(
                title = it.title ?: "Droga Krzyżowa",
                message = it.body ?: "",
                isPromo = isPromo
            )
        }
    }

    private fun showNotification(title: String, message: String, isPromo: Boolean) {
        val channelId = "dk_notifications_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 1. Zapisujemy flagę promocyjną do SharedPreferences
        if (isPromo) {
            val sharedPref = getSharedPreferences("promotion_prefs", Context.MODE_PRIVATE)
            sharedPref.edit {
                putString(
                    "pending_store_url",
                    "market://details?id=mivs.mojaparafia"
                )
            }
        }

        // 2. Intent ZAWSZE kieruje do Twojej MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(), // Unikalne ID
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TEST", "Nowy token Drogi Krzyżowej: $token")
    }
}