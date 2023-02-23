package com.zebra.nilac.csvbarcodelookup.ui.init

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.*
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject

class InitActivity : AppCompatActivity() {

    private val initViewModel: InitViewModel by viewModels()
    private lateinit var mBinder: ActivityInitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityInitBinding.inflate(layoutInflater)
        setContentView(mBinder.root)

        init()
    }

    private fun init() {
        Log.i(TAG, "Initialising Application...")
        mBinder.loadingStatusText.text = getString(R.string.init_screen_emdk_check)

        initViewModel.isDWProfileGenerated.observe(this, dwProfileObserver)
        initViewModel.isDataImported.observe(this, dataImportObserver)

        if (Environment.isExternalStorageManager()) {
            mBinder.loadingStatusText.text =
                getString(R.string.init_process_storage_permission_profile_success)
            checkIfDWProfileExists()
        } else {
            mBinder.loadingStatusText.text =
                getString(R.string.init_process_storage_permission_profile_failed)
        }
    }

    private fun checkIfDWProfileExists() {
        Log.i(TAG, "Checking if DW Profile exists...")

        //Register the receiver
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val extras = intent.extras
                if (extras!!.containsKey("com.symbol.datawedge.api.RESULT_GET_CONFIG")) {
                    val bundle = intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_CONFIG")
                    if (bundle != null &&
                        !bundle.isEmpty &&
                        bundle.getString("PROFILE_NAME") != null
                    ) {
                        Log.i(TAG, "DW Profile already exists, skipping creation part")
                        initViewModel.importData()
                    } else {
                        Log.w(
                            TAG,
                            "DW Profile not found, proceeding with the creation through ProfileManager"
                        )
                        initViewModel.createDWProfile()
                    }
                }
            }
        }, IntentFilter().apply {
            addAction("com.symbol.datawedge.api.RESULT_ACTION")
            addCategory(Intent.CATEGORY_DEFAULT)
        })

        val dwIntent = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra("com.symbol.datawedge.api.GET_CONFIG", Bundle().apply {
                putString("PROFILE_NAME", AppConstants.DW_PROFILE_NAME)
                putBundle("PLUGIN_CONFIG", Bundle().apply {
                    putStringArrayList("PLUGIN_NAME", arrayListOf<String>().apply {
                        add("BARCODE")
                    })
                })
            })
        }

        sendBroadcast(dwIntent)
    }

    private val dwProfileObserver: Observer<Boolean> =
        Observer { isGenerated ->
            if (isGenerated) {
                mBinder.loadingStatusText.text =
                    getString(R.string.init_process_dw_profile_success)
                initViewModel.importData()
            } else {
                mBinder.loadingStatusText.text =
                    getString(R.string.init_process_dw_profile_failed)
            }
        }

    private val dataImportObserver: Observer<DataImportObject> =
        Observer { importObject ->
            if (importObject.isCSVDataImported && importObject.isContainerLocationsImported) {
                finish()
                startActivity(Intent(this@InitActivity, MainActivity::class.java))
            } else {
                mBinder.loadingStatusText.text =
                    getString(R.string.init_process_load_csv_data_failed, importObject.errorMessage)
            }
        }

    companion object {
        const val TAG = "InitActivity"
    }
}