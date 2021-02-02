package com.android.chatanalyzer.import_chat

import android.util.JsonReader
import android.util.JsonToken
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.chatanalyzer.chat.Chat
import com.android.chatanalyzer.chat.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.*

class ImportChatViewModel : ViewModel() {

    private var reader: JsonReader? = null

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _chat = MutableLiveData<Chat?>(null)
    val chat: LiveData<Chat?> get() = _chat

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
    fun readChat() {
        if (reader == null)
            throw NullPointerException("Open file with chat first - 'openNewChat(jsonReader)'")
        reader!!.let {

            _chat.value = null
            _isLoading.value = true

            GlobalScope.launch {
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

                reader = null

                _chat.postValue(Chat(users.elementAt(0), users.elementAt(1), messages))
                _isLoading.postValue(false)
            }
        }
    }
}