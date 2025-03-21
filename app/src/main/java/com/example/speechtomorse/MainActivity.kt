package com.example.speechtomorse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : ComponentActivity() {
    private var recognizedText by mutableStateOf("")
    private var morseCode by mutableStateOf("")
    private var errorMessage by mutableStateOf<String?>(null)
    private var isListening by mutableStateOf(false)

    private val speechRecognizer = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == RESULT_OK && result.data != null) {
            val spokenText = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )?.get(0) ?: ""
            recognizedText = spokenText
            morseCode = MorseCodeConverter.convert(spokenText)
        } else {
            errorMessage = getString(R.string.error_speech_timeout)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognition()
        } else {
            errorMessage = getString(R.string.error_permission_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SpeechToMorseScreen(
                        recognizedText = recognizedText,
                        morseCode = morseCode,
                        isListening = isListening,
                        errorMessage = errorMessage,
                        onStartListening = ::checkPermissionAndStartRecognition,
                        onClear = ::clearState
                    )
                }
            }
        }
    }

    private fun checkPermissionAndStartRecognition() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startSpeechRecognition()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startSpeechRecognition() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.start_listening))
            }
            isListening = true
            speechRecognizer.launch(intent)
        } catch (e: Exception) {
            errorMessage = getString(R.string.error_speech_not_available)
            isListening = false
        }
    }

    private fun clearState() {
        recognizedText = ""
        morseCode = ""
        errorMessage = null
        isListening = false
    }
}