package com.kakapo.asyncwars.view

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
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

    var handlerThread: HandlerThread? = null
    var single: Disposable? = null

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

            runUiBlockingProcessing()
        }
    }



    //---------- life cycle --------------//

    override fun onStart() {
        super.onStart()
        BroadcasterUtils.unregisterReceiver(this, mReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread?.quit()
        single?.dispose()
    }

    //---------- helper method --------------//

    fun setMethodBeingUsedInUi(method: String){
        mBinding.textViewMethodUsed.text = resources.getString(R.string.download_image_method, method)
    }

    fun fibonacci(number: Int) : Long{
        return if(number == 1|| number == 2){
            1
        }else{
            fibonacci(number - 1) + fibonacci(number - 2)
        }
    }

    //---------- async method --------------//
    fun runUiBlockingProcessing(){
        showToast("Result: ${fibonacci(40)}")
    }

    fun getImageUsingThread(){
        val thread = Thread(myRunnable)
        thread.start()
    }

    fun getImageUsingIntentService(){
        val intent = Intent(this, MyIntentService::class.java)
        startActivity(intent)
    }

    fun getImageFromExecutors(){
        val executor = Executors.newFixedThreadPool(4)
        executor.submit(myRunnable)
    }

    fun getImageUsingAsyncTask(){
        val mAsyncTask = GetImageAsyncTask(imageDownloadListener)
        @Suppress("DEPRECATION")
        mAsyncTask.execute()
    }

    fun getImageUsingHandler(){
        val uiHandler = Handler(Looper.getMainLooper())

        Thread{
            val bitmap = DownloaderUtils.downloadImage()

            uiHandler.post{
                mBinding.imageView.setImageBitmap(bitmap)
            }
        }.start()
    }

    fun getImageUsingHandlerThread(){
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

    fun getImageUsingRx(){
        single = Single.create<Bitmap>{ emiter ->
            DownloaderUtils.downloadImage()?.let{ bitmap ->
                emiter.onSuccess(bitmap)
            }
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { bitmap ->
                mBinding.imageView.setImageBitmap(bitmap)
            }
    }

    fun getImageUsingCoroutine(){
        //TODO: add implementation here
    }
    //run async task
    inner class MyRunnable: Runnable{
        override fun run() {
            val bitmap = DownloaderUtils.downloadImage()

            mBinding.imageView.setImageBitmap(bitmap)
        }

    }
}