package com.kakapo.asyncwars.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager

internal object BroadcasterUtils {

    fun sendBitmap(context: Context, bmp: Bitmap?){
        val intent = Intent()
        bmp?.let {
            intent.putExtra(Constants.IMAGE_BITMAP, it)
            intent.action = Constants.FILTER_ACTION_KEY
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    fun registerReceiver(context: Context, myBroadcastReceiver: MyBroadCastReceiver){

    }
}