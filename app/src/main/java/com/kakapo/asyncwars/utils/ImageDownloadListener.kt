package com.kakapo.asyncwars.utils

import android.graphics.Bitmap

interface ImageDownloadListener {
    fun onSuccess(bitmap: Bitmap?)
}