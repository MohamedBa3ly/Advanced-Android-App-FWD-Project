package com.example.advancedapp

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.advancedapp.databinding.ActivityMainBinding
import com.example.advancedapp.utils.NotificationUtils

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var downloadStatus = "Fail"
    private lateinit var selectedDownload : LoadURL
    private val notificationID = 0



    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup toolbar:
        setSupportActionBar(binding.toolbar)

        //Create Notification Channel First :
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(this,NotificationUtils.getFilesChannel(this))
        }

        // i have something downloaded please show it to me , (register receiver)  :)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //Before downloading choose one file to download from radio button :
        binding.includeMain.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            selectedDownload = when(i){
                R.id.radio_retrofit -> LoadURL.RETROFIT_URI
                R.id.radio_project -> LoadURL.UDACITY_URI
                R.id.radio_glide -> LoadURL.GLIDE_URI
                else -> {LoadURL.RETROFIT_URI}
            }
        }

        //when internet is found so you can download file when you click on Button :)
        binding.includeMain.customButton.setOnClickListener {

            if (this::selectedDownload.isInitialized){

                val connectivityManager =
                    this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val capabilities =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }
                if (capabilities != null) {
                    binding.includeMain.customButton.buttonState = ButtonState.Loading
                    download()
                }else{
                    //First check ur internet : ( Toast message )
                    Toast.makeText(this,"Check your internet",Toast.LENGTH_LONG).show()
                }
            }else{
                //here u should choose file first ( Radio Button ) :
                Toast.makeText(this,getString(R.string.select_file_first),Toast.LENGTH_LONG).show()
            }

        }

    }

    //After download done i will send notification :
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID==id){
                downloadStatus = "Success"
                binding.includeMain.customButton.buttonState = ButtonState.Completed
                notificationSending()
            }
        }
    }

    //Fun to Download including status successful and complete :
    private fun download() {
        val request =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DownloadManager.Request(Uri.parse(selectedDownload.uri))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
            } else {
                TODO("VERSION.SDK_INT < N")
            }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()){
            when(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)){

                DownloadManager.STATUS_FAILED -> {
                    downloadStatus = "Fail"
                    binding.includeMain.customButton.buttonState = ButtonState.Completed
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloadStatus = "Success"
                }
            }
        }
    }

    //companion object for three uri that i can download from it , i add title and text to show in notification that will appear when download done:
    companion object {

         enum class LoadURL (val uri: String, val title: String, val text: String) {
            GLIDE_URI(
                "https://github.com/bumptech/glide/archive/master.zip",
                "Glide: Image Loading Library By BumpTech",
                "Glide repository is downloaded"
            ),
            UDACITY_URI(
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
                "Udacity: Android Kotlin Nanodegree",
                "The Project 3 repository is downloaded"),
            RETROFIT_URI(
                "https://github.com/square/retrofit/archive/master.zip",
                "Retrofit: Type-safe HTTP client by Square, Inc",
                "Retrofit repository is downloaded"),
        }
    }

    //Fun to make a notification and pass all required data ( i am here, from Notification Utils ) :
    fun notificationSending(){
        NotificationUtils.sendNotification(
            context = this@MainActivity,
            titleId = selectedDownload.title,
            stateId = downloadStatus,
            textNotify = selectedDownload.text,
            notificationId = notificationID
        )
    }
}