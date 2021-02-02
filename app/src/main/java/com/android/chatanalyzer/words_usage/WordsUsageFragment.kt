package com.android.chatanalyzer.words_usage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.android.chatanalyzer.R
import com.android.chatanalyzer.databinding.FragmentChatStatsBinding
import com.android.chatanalyzer.databinding.FragmentWordsUsageBinding

class WordsUsageFragment : Fragment() {

    private val viewModel: WordsUsageViewModel by viewModels()

    private lateinit var binding: FragmentWordsUsageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordsUsageBinding.inflate(inflater, container, false)
        val view = binding.root;

        binding.wordsUsageViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return view
    }
}