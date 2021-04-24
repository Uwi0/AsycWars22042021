package com.kakapo.asyncwars.view

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import com.kakapo.asyncwars.R
import com.kakapo.asyncwars.async.GetImageAsyncTask
import com.kakapo.asyncwars.async.MyIntentService
import com.kakapo.asyncwars.databinding.ActivityMainBinding
import com.kakapo.asyncwars.utils.*
import com.kakapo.asyncwars.utils.BroadcasterUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.Executors

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

   private var handlerThread: HandlerThread? = null
    private var single: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely)
        mBinding.contentLoadingProgressBar.startAnimation(rotateAnimation)

        val doProcessingInUiThread = false
        val methodToUse = MethodToDownloadImage.Coroutine

        if (doProcessingInUiThread){
            mBinding.textViewMethodUsed.text = resources.getString(R.string.calculating_fibonacci_number)
        }else{
            when(methodToUse){
                MethodToDownloadImage.Thread -> setMethodBeingUsedInUi("Thread")
                MethodToDownloadImage.AsyncTask -> setMethodBeingUsedInUi("AsyncTask")
                MethodToDownloadImage.IntentService -> setMethodBeingUsedInUi("IntentService")
                MethodToDownloadImage.Handler -> setMethodBeingUsedInUi("Handler")
                MethodToDownloadImage.HandlerThread -> setMethodBeingUsedInUi("handlerThread")
                MethodToDownloadImage.Executor -> setMethodBeingUsedInUi("Executor")
                MethodToDownloadImage.RxJava -> setMethodBeingUsedInUi("RXJava")
                MethodToDownloadImage.Coroutine -> setMethodBeingUsedInUi("Coroutine")
            }
        }


        mBinding.buttonDownloadBitmap.setOnClickListener{
            mBinding.imageView.setImageBitmap(null)
            Log.i("clicked", "button clicked")
            if (doProcessingInUiThread){
                runUiBlockingProcessing()
            }else{
                when (methodToUse) {
                    MethodToDownloadImage.Thread -> getImageUsingThread()
                    MethodToDownloadImage.AsyncTask -> getImageUsingAsyncTask()
                    MethodToDownloadImage.IntentService -> getImageUsingIntentService()
                    MethodToDownloadImage.Handler -> getImageUsingHandler()
                    MethodToDownloadImage.HandlerThread -> getImageUsingHandlerThread()
                    MethodToDownloadImage.Executor -> getImageFromExecutors()
                    MethodToDownloadImage.RxJava -> getImageUsingRx()
                    MethodToDownloadImage.Coroutine -> getImageUsingCoroutine()
                }
            }
        }
    }

    //---------- life cycle --------------//

    override fun onStart() {
        super.onStart()
        BroadcasterUtils.registerReceiver(this, mReceiver)
    }

    override fun onStop() {
        super.onStop()
        BroadcasterUtils.unregisterReceiver(this, mReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread?.quit()
        single?.dispose()
    }

    //---------- helper method --------------//

    private fun setMethodBeingUsedInUi(method: String){
        mBinding.textViewMethodUsed.text = resources.getString(R.string.download_image_method, method)
    }

    private fun fibonacci(number: Int) : Long{
        return if(number == 1|| number == 2){
            1
        }else{
            fibonacci(number - 1) + fibonacci(number - 2)
        }
    }

    //---------- async method --------------//
    private fun runUiBlockingProcessing(){
        showToast("Result: ${fibonacci(40)}")
    }

    private fun getImageUsingThread(){
        val thread = Thread(myRunnable)
        thread.start()
    }

    private fun getImageUsingIntentService(){
        val intent = Intent(this, MyIntentService::class.java)
        startService(intent)
    }

    private fun getImageFromExecutors(){
        val executor = Executors.newFixedThreadPool(4)
        executor.submit(myRunnable)
    }

    private fun getImageUsingAsyncTask(){
        val mAsyncTask = GetImageAsyncTask(imageDownloadListener)
        @Suppress("DEPRECATION")
        mAsyncTask.execute()
    }

    private fun getImageUsingHandler(){
        val uiHandler = Handler(Looper.getMainLooper())

        Thread{
            val bitmap = DownloaderUtils.downloadImage()

            uiHandler.post{
                mBinding.imageView.setImageBitmap(bitmap)
            }
        }.start()
    }

    private fun getImageUsingHandlerThread(){
        handlerThread = HandlerThread(Constants.MY_HANDLER_THREAD)

        handlerThread?.let{
            it.start()
            val looper = it.looper
            val handler = Handler(looper)
            handler.post{
                val bitmap = DownloaderUtils.downloadImage()

                BroadcasterUtils.sendBitmap(applicationContext, bitmap)
            }
        }
    }

    private fun getImageUsingRx(){
        single = Single.create<Bitmap>{ emitter ->
            DownloaderUtils.downloadImage()?.let{ bitmap ->
                emitter.onSuccess(bitmap)
            }
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { bitmap ->
                mBinding.imageView.setImageBitmap(bitmap)
            }
    }

    private fun getImageUsingCoroutine(){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val deferredJob = async(Dispatchers.IO) {
                DownloaderUtils.downloadImage()
            }
            withContext(Dispatchers.Main){
                val bitmap = deferredJob.await()
                mBinding.imageView.setImageBitmap(bitmap)
            }
        }
    }
    //run async task
    inner class MyRunnable: Runnable{
        override fun run() {
            val bitmap = DownloaderUtils.downloadImage()

            runOnUiThread{
                mBinding.imageView.setImageBitmap(bitmap)
            }
        }

    }
}