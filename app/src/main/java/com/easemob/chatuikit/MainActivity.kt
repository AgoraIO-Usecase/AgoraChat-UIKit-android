package com.easemob.chatuikit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easemob.chatuikit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnClick.setOnClickListener {

        }
    }
}