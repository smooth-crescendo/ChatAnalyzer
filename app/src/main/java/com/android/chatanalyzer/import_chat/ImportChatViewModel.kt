package com.android.chatanalyzer.import_chat

import android.util.JsonReader
import androidx.lifecycle.ViewModel
import com.android.chatanalyzer.chat.Chat

class ImportChatViewModel : ViewModel() {

    var reader: JsonReader? = null

    var editedMessages = 0
    var totalMessages = 0

    /**
     * opens new chat associated with this jsonReader
     */
    fun openNewChat(jsonReader: JsonReader) {
        reader = jsonReader
        editedMessages = 0
        totalMessages = 0
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

            it.beginObject()
            while (it.hasNext()) {
                when (it.nextName()) {
                    "messages" -> {
                        it.beginArray()
                        while (it.hasNext()) {
                            it.beginObject()
                            totalMessages++
                            while (it.hasNext()) {
                                when (it.nextName()) {
                                    "from" -> users = users.plus(it.nextString())
                                    "from_id" -> {
                                        val id = it.nextLong()
                                    }
                                    "edited" -> {
                                        it.skipValue()
                                        editedMessages++
                                    }
                                    else -> it.skipValue()
                                }
                            }
                            it.endObject()
                        }
                        it.endArray()
                    }
                    else -> it.skipValue()
                }
            }
            it.endObject()

            return Chat(users.elementAt(0), users.elementAt(1))
        }
    }
}