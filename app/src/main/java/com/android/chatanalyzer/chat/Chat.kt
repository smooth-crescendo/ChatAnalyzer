package com.android.chatanalyzer.chat

class Chat private constructor(
    val user: String,
    val withUser: String,
    val messages: ArrayList<Message>
) {

    companion object {

        @Volatile
        private var INSTANCE: Chat? = null

        fun get(): Chat? {
            return INSTANCE
        }

        fun create(user: String, withUser: String, messages: ArrayList<Message>)
        {
            INSTANCE = Chat(user, withUser, messages)
        }
    }
}
