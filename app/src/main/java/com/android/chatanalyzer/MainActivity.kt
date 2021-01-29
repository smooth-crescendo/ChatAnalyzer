package com.android.chatanalyzer

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.chatanalyzer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val PICK_CHAT_JSON_FILE = 2

    private fun openFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }

        startActivityForResult(intent, PICK_CHAT_JSON_FILE)
    }

    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 3)
        }

        binding.importChatButton.setOnClickListener {
            openFile()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {

        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == PICK_CHAT_JSON_FILE
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                val contentResolver = contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val reader = inputStream?.reader()
                val contents = reader?.readText()
                binding.text.text = contents?.substring(1..600)

            }
        }
    }
}