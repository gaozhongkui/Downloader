package com.almighty.downloader

import android.app.Application

class TheApp : Application() {

    init {
        instance = this
    }


    companion object {
        private const val TAG = "TheApp"
        private lateinit var instance: TheApp

        @JvmStatic
        fun getInstance() = instance
    }
}