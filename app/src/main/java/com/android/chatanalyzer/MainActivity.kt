package com.android.chatanalyzer

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.JsonReader
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.chatanalyzer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var jsonReader: JsonReader
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
        }

        binding.importChatButton.setOnClickListener {
            requestUserOpenJsonFile()

            binding.allMessageKeys.visibility = View.GONE
            binding.importedChatType.visibility = View.GONE
            binding.readAllMessageKeysButton.visibility = View.GONE
            binding.readAllMessageKeysButton.isEnabled = true
        }

        binding.readAllMessageKeysButton.setOnClickListener {
            val propertiesSet = readChat()
            binding.readAllMessageKeysButton.isEnabled = false

            binding.allMessageKeys.text = propertiesSet.toString()
            binding.allMessageKeys.visibility = View.VISIBLE
        }
    }

    private fun requestUserOpenJsonFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, PICK_CHAT_JSON_FILE)
    }

    /**
     * reads a json object
     * @return list of all encountered keys
     */
    private fun readJsonObject(): ArrayList<String> {
        val objectKeys = arrayListOf<String>()
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            val property = jsonReader.nextName()
            jsonReader.skipValue()
            objectKeys.add(property)
        }
        jsonReader.endObject()
        return objectKeys
    }

    /**
     * reads a json representation of a chat (works only with telegram chats by now)
     * @return set of all encountered keys
     */
    private fun readChat(): Set<String> {
        var propertiesSet = setOf<String>()

        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            when (jsonReader.nextName()) {
                "messages" -> {
                    jsonReader.beginArray()
                    while (jsonReader.hasNext()) {
                        val keys = readJsonObject()
                        for (key: String in keys) {
                            propertiesSet = propertiesSet.plus(key)
                        }
                    }
                    jsonReader.endArray()
                }
                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()

        return propertiesSet
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == PICK_CHAT_JSON_FILE
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                val contentResolver = contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val reader = inputStream?.reader()
                jsonReader = JsonReader(reader)

                binding.importedChatType.text = "Telegram chat"
                binding.importedChatType.visibility = View.VISIBLE

                binding.readAllMessageKeysButton.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val PICK_CHAT_JSON_FILE = 2
        const val REQUEST_READ_EXTERNAL_STORAGE = 3
    }
}