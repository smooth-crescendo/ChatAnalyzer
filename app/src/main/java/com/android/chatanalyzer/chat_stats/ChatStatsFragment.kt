package com.android.chatanalyzer.chat_stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.android.chatanalyzer.R
import com.android.chatanalyzer.databinding.FragmentChatStatsBinding
import com.android.chatanalyzer.main_activity.ChatModel

class ChatStatsFragment : Fragment() {

    private val viewModel: ChatStatsViewModel by viewModels()
    private val model: ChatModel by activityViewModels()

    private var _binding: FragmentChatStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatStatsBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        model.chat?.let {
            binding.chatTitle.text = getString(R.string.chat_title, it.user)
            binding.chatSubtitle.text = getString(R.string.chat_subtitle, it.with_user, "Telegram")
        }

        return view
    }
}