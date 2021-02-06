package com.android.chatanalyzer.words_usage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.chatanalyzer.chat.Chat

class WordsUsageViewModel : ViewModel() {

    private var topMessages = MutableLiveData<ArrayList<String>>(arrayListOf())
    var topMessagesStr = Transformations.map(topMessages) {
        it.toString()
    }

    fun calculateTopMessages() {
        Chat.get()?.let {
            for (i in 0..100) {
                val message = it.messages[i];
                topMessages.value!!.add(message.text)
            }
        }
    }
}