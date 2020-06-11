package com.example.myapplication

import android.os.Build
import java.io.Serializable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Message(
    var messageId: String,
    var messageText: String,
    private var messageTime: String,
    var messageStatus: Boolean
) : Serializable {
    var messageFormatedTime: String

    init {
        messageFormatedTime = convertTime()
    }

    private fun convertTime() : String {
        val zonedDateTime: ZonedDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZonedDateTime.parse(messageTime)
        } else {
            return messageTime
        }
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))
    }

}