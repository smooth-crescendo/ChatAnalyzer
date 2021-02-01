package com.android.chatanalyzer.import_chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.JsonReader
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.android.chatanalyzer.main_activity.ChatModel
import com.android.chatanalyzer.chat.Chat
import com.android.chatanalyzer.databinding.FragmentImportChatBinding

class ImportChatFragment : Fragment() {

    private val viewModel: ImportChatViewModel by viewModels()
    private val model: ChatModel by activityViewModels()

    private lateinit var jsonReader: JsonReader
    private var _binding: FragmentImportChatBinding? = null

    var editedMessages = 0
    var totalMessages = 0

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportChatBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.importChatButton.setOnClickListener {
            requestUserOpenJsonFile()

            binding.allMessageKeys.visibility = View.GONE
            binding.importedChatType.visibility = View.GONE
            binding.readAllMessageKeysButton.visibility = View.GONE
            binding.readAllMessageKeysButton.isEnabled = true
            binding.analyzeChatButton.visibility = View.GONE
        }

        binding.readAllMessageKeysButton.setOnClickListener {
            model.chat = readChat()
            binding.readAllMessageKeysButton.isEnabled = false

            val editedMessagesPercentage =
                ((editedMessages.toDouble() / totalMessages.toDouble()) * 100)

            binding.allMessageKeys.text =
                "%d/%d (%.2f%%)".format(editedMessages, totalMessages, editedMessagesPercentage)
            binding.allMessageKeys.visibility = View.VISIBLE
        }

        binding.analyzeChatButton.setOnClickListener {
            val action = ImportChatFragmentDirections.actionImportChatToChatStats()
            binding.root.findNavController().navigate(action)
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

    /**
     * reads a json representation of a chat (works only with telegram chats by now)
     * @return Chat
     */
    private fun readChat(): Chat {
        var users = setOf<String>()

        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            when (jsonReader.nextName()) {
                "messages" -> {
                    jsonReader.beginArray()
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject()
                        totalMessages++
                        while (jsonReader.hasNext()) {
                            when (jsonReader.nextName()) {
                                "from" -> users = users.plus(jsonReader.nextString())
                                "from_id" -> {
                                    val id = jsonReader.nextLong()
                                }
                                "edited" -> {
                                    jsonReader.skipValue()
                                    editedMessages++
                                }
                                else -> jsonReader.skipValue()
                            }
                        }
                        jsonReader.endObject()
                    }
                    jsonReader.endArray()
                }
                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()

        return Chat(users.elementAt(0), users.elementAt(1))
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
                jsonReader = JsonReader(reader)

                editedMessages = 0
                totalMessages = 0

                binding.importedChatType.text = "Telegram chat"
                binding.importedChatType.visibility = View.VISIBLE

                binding.analyzeChatButton.visibility = View.VISIBLE

                binding.readAllMessageKeysButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PICK_CHAT_JSON_FILE = 2
        const val REQUEST_READ_EXTERNAL_STORAGE = 3
    }
}