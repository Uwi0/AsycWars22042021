package com.kakapo.asyncwars.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap

class MyBroadCastReceiver(val imageDownloadListener: ImageDownloadListener): BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent) {
        val bitmap = intent.getParcelableExtra<Bitmap>(Constants.IMAGE_BITMAP)

        imageDownloadListener.onSuccess(bitmap)
    }

}