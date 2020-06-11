package com.example.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_message.*
import okhttp3.*
import okhttp3.FormBody
import org.json.JSONException
import java.io.IOException


class MessageActivity : AppCompatActivity() {

    private var selectedMessage: Message? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        selectedMessage = intent.getSerializableExtra("MESSAGE") as Message

        tv_message_text?.text = selectedMessage?.messageText
        tv_message_time?.text = selectedMessage?.messageFormatedTime

        if (selectedMessage?.messageStatus!!) {
            btn_message_status?.text = "CONFIRMED"
            btn_message_status?.isEnabled = false
        } else {
            btn_message_status?.text = "CONFIRM"
        }

        btn_message_status.setOnClickListener {
            confirmMessage()
            btn_message_status.text = "CONFIRMED"
            btn_message_status.isEnabled = false
        }
    }

    private fun confirmMessage() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val serverIp = sharedPreferences.getString("server_ip", "localhost")
        val serverPort = sharedPreferences.getString("server_port", "8000")
        val messageId = selectedMessage?.messageId
        val deviceId = sharedPreferences.getString("device_id", "1")

        val url = "http://$serverIp:$serverPort/api/confirm/"

        val client = OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("device", "$deviceId")
            .add("message", "$messageId")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    val strResponse  = response.body!!.string()
                    Log.d("message", strResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MessageActivity, "Failed to connect to $serverIp:$serverPort", Toast.LENGTH_LONG).show()
                    btn_message_status.text = "CONFIRM"
                    btn_message_status.isEnabled = true
                }
            }
        })

    }
}