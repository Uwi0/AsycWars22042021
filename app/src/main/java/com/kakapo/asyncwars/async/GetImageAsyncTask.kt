package com.kakapo.asyncwars.async

import android.graphics.Bitmap
import android.os.AsyncTask
import com.kakapo.asyncwars.utils.DownloaderUtils
import com.kakapo.asyncwars.utils.ImageDownloadListener

@Suppress("DEPRECATION")
class GetImageAsyncTask(val imageDownloadListener: ImageDownloadListener): AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg params: String?): Bitmap? {
        return DownloaderUtils.downloadImage()
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if (isCancelled){
            return
        }

        imageDownloadListener.onSuccess(result)
        cancel(false)
    }
}