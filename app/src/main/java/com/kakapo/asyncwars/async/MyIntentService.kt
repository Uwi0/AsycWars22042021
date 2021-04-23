package com.kakapo.asyncwars.async

import android.app.IntentService
import android.content.Intent
import com.kakapo.asyncwars.utils.BroadcasterUtils
import com.kakapo.asyncwars.utils.Constants
import com.kakapo.asyncwars.utils.DownloaderUtils

@Suppress("DEPRECATION")
class MyIntentService : IntentService(Constants.MY_INTENT_SERVICE) {
    override fun onHandleIntent(intent: Intent?) {
        val bmp = DownloaderUtils.downloadImage()

        BroadcasterUtils.sendBitmap(applicationContext, bmp+)
    }
}