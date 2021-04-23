package com.kakapo.asyncwars.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

object DownloaderUtils {

    fun downloadImage(): Bitmap? {
        val url = Constants.IMAGE_URL
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()

        var response: Response? = null
        var bitmap: Bitmap? = null

        try{
            response = client.newCall(request).execute()
        }catch (e: IOException){
            Log.e("DownloaderUtils", "Error cannot get response")
            e.printStackTrace()
        }

        response?.apply {
            if (isSuccessful){
                try{
                    bitmap = BitmapFactory.decodeStream(body()?.byteStream())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

            close()
        }

        return bitmap
    }
}