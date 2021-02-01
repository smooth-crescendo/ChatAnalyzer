package com.android.chatanalyzer.import_chat

import android.os.Build
import android.util.JsonReader
import android.util.JsonToken
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.android.chatanalyzer.chat.Chat
import com.android.chatanalyzer.chat.Message
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class ImportChatViewModel : ViewModel() {

    var reader: JsonReader? = null

    /**
     * opens new chat associated with this jsonReader
     */
    fun openNewChat(jsonReader: JsonReader) {
        reader = jsonReader
    }

    /**
     * reads a json representation of a chat (works only with telegram chats by now)
     * @return Chat
     */
    fun readChat(): Chat {
        if (reader == null)
            throw NullPointerException("Open file with chat first - 'openNewChat(jsonReader'")
        reader!!.let {
            var users = setOf<String>()

            val messages = arrayListOf<Message>()

            it.beginObject()
            while (it.hasNext()) {
                when (it.nextName()) {
                    "messages" -> {
                        it.beginArray()
                        while (it.hasNext()) {
                            it.beginObject()
                            var from_id: Long = 0
                            lateinit var date: LocalDateTime
                            var message: String = ""
                            while (it.hasNext()) {
                                when (it.nextName()) {
                                    "from" -> users = users.plus(it.nextString())
                                    "from_id" -> from_id = it.nextLong()
                                    "date" -> {
                                        date = LocalDateTime.ofInstant(
                                            Instant.parse(
                                                it.nextString() + "Z"
                                            ), ZoneOffset.UTC
                                        )
                                    }
                                    "text" -> {
                                        if (it.peek() == JsonToken.BEGIN_ARRAY) {
                                            it.skipValue()
                                        } else {
                                            message = it.nextString()
                                        }
                                    }
                                    else -> it.skipValue()
                                }
                            }
                            it.endObject()
                            messages.add(Message(from_id, date, message))
                        }
                        it.endArray()
                    }
                    else -> it.skipValue()
                }
            }
            it.endObject()

            return Chat(users.elementAt(0), users.elementAt(1), messages)
        }
    }
}