package com.zebra.nilac.csvbarcodelookup.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.ui.main.SettingsActivity
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import java.io.File

class InitActivity : AppCompatActivity() {

    private lateinit var mBinder: ActivityInitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityInitBinding.inflate(layoutInflater)

        setContentView(mBinder.root)

        initEMDKManager()
    }

    private fun initEMDKManager() {
        //Initialising EMDK First...
        Log.i(TAG, "Initialising EMDK Manager")
        mBinder.loadingStatusText.text = getString(R.string.init_screen_emdk_check)

        EMDKLoader.getInstance().initEMDKManager(this, object : EMDKManagerInitCallBack {
            override fun onFailed(message: String) {
                Log.e(TAG, "Failed to initialise EMDK Manager")
                mBinder.loadingStatusText.text = getString(R.string.init_screen_emdk_failed)
            }

            override fun onSuccess() {
                Log.i(TAG, "EMDK Manager was successfully initialised")
                mBinder.loadingStatusText.text = getString(R.string.init_screen_emdk_success)

                grantManageExternalStoragePermission()
            }
        })
    }

    private fun grantManageExternalStoragePermission() {
        Log.i(TAG, "Granting External Storage Permission through MX")
        ProfileLoader().processProfile(
            "AccessManager",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(message: String) {
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(R.string.init_process_storage_permission_profile_failed)
                    }
                }

                override fun onProfileLoaded() {
                    runOnUiThread {
                        if (Environment.isExternalStorageManager()) {
                            mBinder.loadingStatusText.text =
                                getString(R.string.init_process_storage_permission_profile_success)
                        } else {
                            mBinder.loadingStatusText.text =
                                getString(R.string.init_process_storage_permission_profile_failed)
                        }
                    }
                    checkIfDWProfileExists()
                }
            })
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
                        importCsvData()
                    } else {
                        Log.w(
                            TAG,
                            "DW Profile not found, proceeding with the creation through ProfileManager"
                        )
                        createDWProfile()
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
                        add("INTENT")
                    })
                })
            })
        }

        sendBroadcast(dwIntent)
    }

    private fun createDWProfile() {
        ProfileLoader().processProfile(
            AppConstants.DW_PROFILE_NAME,
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(message: String) {
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(R.string.init_process_dw_profile_failed)
                    }
                }

                override fun onProfileLoaded() {
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(R.string.init_process_dw_profile_success)
                    }
                    importCsvData()
                }
            })
    }

    private fun importCsvData() {
        Log.i(TAG, "Importing data from CSV File...")
        mBinder.loadingStatusText.text =
            getString(R.string.init_process_load_csv_data)

        val csvFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/${AppConstants.FOLDER_NAME}/excel-to-load.csv")
        if (!csvFile.exists()) {
            Log.i(
                TAG,
                "No CSV File found at specified destination, assuming there's nothing to update!"
            )
            finish()
            startActivity(Intent(this@InitActivity, SettingsActivity::class.java))
            return
        }

        ExcelDataExtractor.extractDataFromFile(
            csvFile, object : ExcelDataExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported data from CSV file...")
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(R.string.init_process_load_csv_data_success)
                    }

                    //Delete the imported file
                    csvFile.delete()

                    finish()
                    startActivity(Intent(this@InitActivity, SettingsActivity::class.java))
                }

                override fun onFailed(errorMessage: String) {
                    Log.e(TAG, "Failed to load CSV data:\n$errorMessage")
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(
                                R.string.init_process_load_csv_data_failed,
                                errorMessage
                            )
                    }
                }
            })
    }

    companion object {
        const val TAG = "InitActivity"
    }
}