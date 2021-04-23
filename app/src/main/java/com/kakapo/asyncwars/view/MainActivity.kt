package com.kakapo.asyncwars.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import com.kakapo.asyncwars.R
import com.kakapo.asyncwars.databinding.ActivityMainBinding
import com.kakapo.asyncwars.utils.ImageDownloadListener
import com.kakapo.asyncwars.utils.MyBroadCastReceiver

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
            mBinding.imageView.setImageBitmap(bitmap)
        }
    }

    private val mReceiver = MyBroadCastReceiver(imageDownloadListener)
    private val myRunnable = MyRunnable()

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely)
        mBinding.contentLoadingProgressBar.startAnimation(rotateAnimation)

        val doPrecessingInUiThread = true
        val methodToUse = MethodToDownloadImage.Thread

        if (doPrecessingInUiThread){
            mBinding.textViewMethodUsed.text = resources.getString(R.string.calculating_fibonacci_number)
        }else{
            when(methodToUse){
                MethodToDownloadImage.Thread ->
            }
        }
    }

    fun setMethodBeingUsedInUi(method: String){
        mBinding.textViewMethodUsed.text = res
    }

    inner class MyRunnable{

    }
}