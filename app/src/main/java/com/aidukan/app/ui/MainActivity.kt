package com.aidukan.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Simple text view for now
        val textView = android.widget.TextView(this)
        textView.text = "Aidukan App - Voice Assistant for Kirana Stores"
        textView.textSize = 20f
        textView.setPadding(50, 50, 50, 50)
        
        setContentView(textView)
    }
}
