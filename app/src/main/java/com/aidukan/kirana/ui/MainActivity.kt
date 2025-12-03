package com.aidukan.kirana.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aidukan.kirana.R
import com.aidukan.kirana.databinding.ActivityMainBinding
import com.aidukan.kirana.utils.VoiceCommandProcessor
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private var isListening = false
    
    private val PERMISSION_REQUEST_CODE = 100
    
    private val shopMenuItems = listOf(
        "Bills / Invoices",
        "Balance Sheet",
        "Expenses",
        "Stock / Inventory",
        "Reports",
        "Customers / Udhaar",
        "Suppliers",
        "Add Product",
        "Sales Summary"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize TTS
        textToSpeech = TextToSpeech(this, this)
        
        // Check permissions
        checkPermissions()
        
        // Setup UI
        setupShopMenu()
        setupVoiceAssistant()
        setupSidebarButtons()
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            initializeSpeechRecognizer()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeSpeechRecognizer()
            } else {
                Toast.makeText(this, "Permissions required for voice features", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    updateVoiceUI(true, "Listening...")
                }
                
                override fun onBeginningOfSpeech() {}
                
                override fun onRmsChanged(rmsdB: Float) {
                    // Update waveform visualization here
                }
                
                override fun onBufferReceived(buffer: ByteArray?) {}
                
                override fun onEndOfSpeech() {
                    updateVoiceUI(false, getString(R.string.voice_tap_to_speak))
                }
                
                override fun onError(error: Int) {
                    updateVoiceUI(false, getString(R.string.voice_tap_to_speak))
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        processVoiceCommand(spokenText)
                    }
                }
                
                override fun onPartialResults(partialResults: Bundle?) {}
                
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }
    
    private fun setupVoiceAssistant() {
        binding.voiceAssistantCard.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        }
    }
    
    private fun startListening() {
        if (!::speechRecognizer.isInitialized) {
            Toast.makeText(this, "Speech recognizer not initialized", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Hindi + English
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer.startListening(intent)
        isListening = true
    }
    
    private fun stopListening() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
        }
        isListening = false
        updateVoiceUI(false, getString(R.string.voice_tap_to_speak))
    }
    
    private fun updateVoiceUI(listening: Boolean, statusText: String) {
        binding.voiceStatusText.text = statusText
        
        if (listening) {
            binding.waveformView.visibility = View.VISIBLE
            binding.voiceTranscriptText.visibility = View.VISIBLE
            // Add pulsing animation to mic icon
            binding.micIcon.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(500)
                .withEndAction {
                    binding.micIcon.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(500)
                        .start()
                }
                .start()
        } else {
            binding.waveformView.visibility = View.GONE
            binding.micIcon.clearAnimation()
            binding.micIcon.scaleX = 1.0f
            binding.micIcon.scaleY = 1.0f
        }
    }
    
    private fun processVoiceCommand(command: String) {
        binding.voiceTranscriptText.text = "\"$command\""
        binding.voiceTranscriptText.visibility = View.VISIBLE
        
        // Process command using VoiceCommandProcessor
        val result = VoiceCommandProcessor.process(command)
        
        // Speak response
        speak(result.response)
        
        // Show result
        Toast.makeText(this, result.response, Toast.LENGTH_LONG).show()
        
        // Navigate to appropriate screen if needed
        when (result.action) {
            "SALE" -> {
                // Navigate to billing screen
                Toast.makeText(this, "Sale recorded: ${result.data}", Toast.LENGTH_LONG).show()
            }
            "STOCK_CHECK" -> {
                // Navigate to inventory screen
                Toast.makeText(this, "Stock info: ${result.data}", Toast.LENGTH_LONG).show()
            }
            "REPORT" -> {
                // Navigate to reports screen
                Toast.makeText(this, "Report: ${result.data}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupShopMenu() {
        shopMenuItems.forEach { menuItem ->
            val menuButton = layoutInflater.inflate(
                R.layout.shop_menu_item,
                binding.shopMenu,
                false
            )
            // Set menu item text and click listener
            binding.shopMenu.addView(menuButton)
        }
    }
    
    private fun setupSidebarButtons() {
        binding.newsCard.setOnClickListener {
            Toast.makeText(this, "News feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.suggestionsCard.setOnClickListener {
            Toast.makeText(this, "AI Suggestions feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(Locale.US)
            }
        }
    }
    
    private fun speak(text: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    override fun onDestroy() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
