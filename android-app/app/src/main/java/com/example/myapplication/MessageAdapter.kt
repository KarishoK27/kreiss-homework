package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_list_item.view.*

class MessageAdapter(private val items: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_list_item, parent, false))

    override fun getItemCount() : Int = items.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(items[position])
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(messages: ArrayList<Message>) {
        items.clear()
        items.addAll(messages)
        notifyDataSetChanged()
    }

    class MessageHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var message: Message? = null

        init {
            view.setOnClickListener(this)
        }

        fun bind(message: Message) {
            this.message = message

            if (message.messageText.length > 50) {
                view.tv_message_text.text = message.messageText.slice(0..50) + "..."
            } else {
                view.tv_message_text.text = message.messageText
            }

            view.tv_message_time.text = message.messageFormatedTime

            if (message.messageStatus) {
                view.tv_message_status.text = "Confirmed message"
                view.tv_message_status.setTextColor(Color.GRAY)
            } else {
                view.tv_message_status.text = "New message"
                view.tv_message_status.setTextColor(Color.RED)
            }

        }

        override fun onClick(v: View?) {
            val context = itemView.context
            val intent = Intent(context, MessageActivity::class.java).apply {
                putExtra("MESSAGE", message)
            }
            context.startActivity(intent)
            Log.d("RecyclerView", "CLICK!")
        }
    }
}