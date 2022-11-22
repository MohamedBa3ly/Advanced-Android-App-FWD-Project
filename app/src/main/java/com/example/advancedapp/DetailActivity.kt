package com.example.advancedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.advancedapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var notificationTitle: String? = null
    private var notificationState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup toolbar:
        setSupportActionBar(binding.toolbar)

        loadData()
        okButton()
        displayNotificationData()
    }

    //Fun to catch data cross bundle (title and state):
    private fun loadData() {
        val extras = intent.extras
        extras?.let {
            notificationTitle = it.getString(EXTRA_TITLE)
            notificationState = it.getString(EXTRA_STATE)
        }
    }

    //Set data of state and title of file  in two text view in Detail Activity :
    private fun displayNotificationData() {
        binding.includeDetail.apply {
            txtFileValue.text = notificationTitle
            txtStatusValue.text = notificationState
        }
    }

    //Fun to finish Detail Activity when press on Ok button :
    private fun okButton() {
        binding.includeDetail.button.setOnClickListener {
            finish()
        }
    }

    //Companion object to use fun withExtras in Notification Utils to pass title and state from there .. go to Notification utils and pass data :)
    companion object {
        private const val EXTRA_TITLE = "notification_title"
        private const val EXTRA_STATE = "notification_state"

        fun withExtras(title: String, state: String): Bundle {
            return Bundle().apply {
                putString(EXTRA_TITLE, title)
                putString(EXTRA_STATE, state)
            }
        }
    }
}