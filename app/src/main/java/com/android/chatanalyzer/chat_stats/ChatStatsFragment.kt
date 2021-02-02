package com.android.chatanalyzer.chat_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.android.chatanalyzer.R
import com.android.chatanalyzer.databinding.FragmentChatStatsBinding
import com.android.chatanalyzer.main_activity.ChatModel

class ChatStatsFragment : Fragment() {

    private val viewModel: ChatStatsViewModel by viewModels()
    private val model: ChatModel by activityViewModels()

    private lateinit var binding: FragmentChatStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatStatsBinding.inflate(inflater, container, false)
        val view = binding.root

        model.chat?.let {
            binding.chatTitle.text = getString(R.string.chat_title, it.user)
            binding.chatSubtitle.text = getString(R.string.chat_subtitle, it.with_user, "Telegram")

            var hours = mutableMapOf<Int, Int>()
            for (h in 0..24) {
                hours = hours.plus(Pair(h, 0)).toMutableMap()
            }
            for (message in model.chat!!.messages) {
                val hour = message.date.hour
                hours[hour] = hours[hour]!! + 1
            }
            var max = 0
            var max_key = 0
            for (h in hours) {
                if (h.value > max) {
                    max = h.value
                    max_key = h.key
                }
            }
            Toast.makeText(context, max_key.toString(), Toast.LENGTH_SHORT).show()
        }
        return view
    }
}