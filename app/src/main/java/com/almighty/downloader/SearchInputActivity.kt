package com.almighty.downloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.almighty.downloader.databinding.ActivitySearchInputLayoutBinding

class SearchInputActivity : AppCompatActivity() {
    private val binding: ActivitySearchInputLayoutBinding by lazy {
        ActivitySearchInputLayoutBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}