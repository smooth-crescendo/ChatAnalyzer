package com.android.chatanalyzer.words_usage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatanalyzer.chat.Chat
import com.android.chatanalyzer.chat.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class WordsUsageViewModel : ViewModel() {

    private var topMessages = MutableLiveData<ArrayList<String>>(arrayListOf())
    var topMessagesStr = Transformations.map(topMessages) {
        it.toString()
    }

    init {
        calculateTopMessages()
    }

    private fun calculateTopMessages() {
        val wordsUsage = hashMapOf<String, Int>()

        Chat.get()?.let { chat ->
            for (m in chat.messages) {
                m.text.split(Regex("\\s+")).forEach { word ->
                    var w = word
                        .replace(Regex("[^\\w]"), "")
                    if (w.length >= 4) {
                        w = w.toLowerCase(Locale.ROOT)
                        wordsUsage.merge(w, 1) { prev: Int, one: Int -> prev + one }
                    }
                }
            }
        }

        val wordsUsageList = wordsUsage.toList()
        for (i in wordsUsageList.indices) {
            topMessages.value!!.add("${i+1} - ${wordsUsageList[i].first} (${wordsUsageList[i].second})\n")
        }
    }
}