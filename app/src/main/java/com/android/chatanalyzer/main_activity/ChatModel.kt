package com.android.chatanalyzer.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.chatanalyzer.chat.Chat
import java.util.*

class ChatModel : ViewModel() {

    var chat: Chat? = null
}