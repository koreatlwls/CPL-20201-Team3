package com.giggle.prototype

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"
    var state = 0
    var pendingIntent: PendingIntent? = null

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sendNotification(remoteMessage.notification!!.title, remoteMessage.notification!!.body,remoteMessage.data.get("shopname"),remoteMessage.data.get("shopposition"))
    }
    // [END receive_message]
    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
    // [END on_new_token]
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(title: String?, messageBody: String?, shopname: String?, shopposition: String?) {
        val intent = Intent(this@MyFirebaseMessagingService, AdDetailApply::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("name", title)

        val intent1 = Intent(this@MyFirebaseMessagingService, MemDetail::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.putExtra("name", title)
        intent1.putExtra("shopname", shopname)
        intent1.putExtra("shopposition",shopposition)

        val intent2 = Intent(this@MyFirebaseMessagingService, SavedAdActivity::class.java)
        intent2.putExtra("shopname", shopname)
        intent2.putExtra("shopposition",shopposition)

        val db = FirebaseFirestore.getInstance()
        db.collection("members")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (title == document.data["name"].toString()) {
                        state = 1
                    }
                }
                //아르바이트 지원시 pendingIntent -> 회원정보 MemDetail 로 이동
                if (state == 1) {
                    pendingIntent = PendingIntent.getActivity(
                        this@MyFirebaseMessagingService, 0 /* Request code */, intent1,
                        PendingIntent.FLAG_ONE_SHOT
                    )

                }
                else if(title == "채용"){
                    val user = FirebaseAuth.getInstance().currentUser
                    var map = mutableMapOf<String,Any>()
                    map["shopname"] = shopname.toString()
                    map["shopposition"] = shopposition.toString()
                    if (user != null) {
                        db.collection("recruit_shop").document(user.uid).update("shop",FieldValue.arrayUnion(map)).addOnCompleteListener{
                            if(it.isSuccessful){
                                println("업데이트")
                            }
                        }
                    }
                    pendingIntent = PendingIntent.getActivity(
                        this@MyFirebaseMessagingService, 0 /* Request code */, intent2,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                }
                //광고 등록시 pendingIntent -> 광고상세정보 AdDetailApply 로 이동
                else {
                    pendingIntent = PendingIntent.getActivity(
                        this@MyFirebaseMessagingService, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                }
                val channelId = "fcm_default_channel"
                val defaultSoundUri = RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_NOTIFICATION
                )
                val notificationBuilder =
                    NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.alert_light_frame)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "fcm_default_channel",
                        "fcm_default_channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
            }
    }
}
