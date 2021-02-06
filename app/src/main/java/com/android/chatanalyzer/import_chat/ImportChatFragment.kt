package com.android.chatanalyzer.import_chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.JsonReader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.android.chatanalyzer.databinding.FragmentImportChatBinding

class ImportChatFragment : Fragment() {

    private val viewModel: ImportChatViewModel by viewModels()

    private lateinit var binding: FragmentImportChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportChatBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.importChatViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        }

        binding.importChatButton.setOnClickListener {
            requestUserOpenJsonFile()
        }

        binding.analyzeChatButton.setOnClickListener {
            val action = ImportChatFragmentDirections.actionImportChatToChatStats()
            view.findNavController().navigate(action)
        }

        return view
    }

    private fun requestUserOpenJsonFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, PICK_CHAT_JSON_FILE)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == PICK_CHAT_JSON_FILE
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                val contentResolver = requireContext().contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val reader = inputStream?.reader()

                viewModel.openNewChat(JsonReader(reader))
                viewModel.readChat()
            }
        }
    }

    companion object {
        const val PICK_CHAT_JSON_FILE = 2
        const val REQUEST_READ_EXTERNAL_STORAGE = 3
    }
}