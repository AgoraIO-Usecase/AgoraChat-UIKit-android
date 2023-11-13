package com.easemob.chatuikit.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easemob.chatuikit.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnClick.setOnClickListener {

        }
    }
}