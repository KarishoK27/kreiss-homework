package com.example.myapplication

import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.*
import org.json.JSONException
import java.io.IOException
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        val serverIp = "localhost"
        val serverPort = "8000"

        val deviceName = android.os.Build.MODEL
        val deviceId = UUID.randomUUID().toString()

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("server_ip", serverIp)
        editor.putString("server_port", serverPort)
        editor.putString("device_name", deviceName)
        editor.putString("device_id", deviceId)
        editor.commit()

        val url = "http://$serverIp:$serverPort/api/devices/"

        val client = OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("name", "$deviceName")
            .add("device_id", "$deviceId")
            .add("registration_id", "$token")
            .add("active", "true")
            .add("type", "android")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    Log.d(TAG, "sendRegistrationTokenToServer($token)")
                    val strResponse  = response.body!!.string()
                    Log.d(TAG, strResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })

    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}