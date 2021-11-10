package com.example.skismoring.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skismoring.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = this.getSharedPreferences("Theme", Context.MODE_PRIVATE)
        theme.applyStyle(sharedPref.getInt("Theme", R.style.Theme_Skismoring_Light), true)
        setContentView(R.layout.activity_main)
    }
}