package com.kakapo.asyncwars.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kakapo.asyncwars.R
import com.kakapo.asyncwars.utils.ImageDownloadListener

class MainActivity : AppCompatActivity() {

    enum class MethodToDownloadImage{
        Thread,
        AsyncTask,
        IntentService,
        Handler,
        HandlerThread,
        Executor,
        RxJava,
        Coroutine
    }

    private val imageDownloadListener = object: ImageDownloadListener{
        override fun onSuccess(bitmap: Bitmap?) {
            
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}