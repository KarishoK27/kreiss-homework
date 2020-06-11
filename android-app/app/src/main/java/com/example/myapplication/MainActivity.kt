package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var messageList: ArrayList<Message> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        rv_message_list.layoutManager = linearLayoutManager
        adapter = MessageAdapter(messageList)
        rv_message_list.adapter = adapter
        getMessages()

        mHandler = Handler()

        swipe_refresh_layout.setOnRefreshListener {
            mRunnable = Runnable {
                getMessages()
                swipe_refresh_layout.isRefreshing = false
            }

            mHandler.post(mRunnable)
        }

        swipe_refresh_layout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMessages() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val serverIp = sharedPreferences.getString("server_ip", "localhost").toString().replace("\\s".toRegex(), "")
        val serverPort = sharedPreferences.getString("server_port", "8000")
        val deviceId = sharedPreferences.getString("device_id", "1")

        val url = "http://$serverIp:$serverPort/api/messages/?device=$deviceId"

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    val strResponse  = response.body!!.string()
                    Log.d(TAG, strResponse)
                    val messageJSONObject = JSONObject(strResponse)
                    val messageJSONArray : JSONArray = messageJSONObject.getJSONArray("results")
                    messageList = ArrayList()

                    for (i in 0 until messageJSONArray.length()) {
                        val message = messageJSONArray.getJSONObject(i)
                        val messageId = message.getString("id")
                        val messageText = message.getString("message_text")
                        val messageTime = message.getString("time")
                        val messageStatus = message.getBoolean("status")
                        messageList.add(Message(messageId, messageText, messageTime, messageStatus))
                    }

                    runOnUiThread {
                        adapter.clear()
                        adapter.addAll(messageList)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to connect to $serverIp:$serverPort", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}