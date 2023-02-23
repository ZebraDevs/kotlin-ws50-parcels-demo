package com.zebra.nilac.csvbarcodelookup.ui.init

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity

class InitActivity : AppCompatActivity() {

    private val initViewModel: InitViewModel by viewModels()
    private lateinit var mBinder: ActivityInitBinding

    private val mSharedPreferences: SharedPreferences =
        DefaultApplication.getInstance().sharedPreferencesInstance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityInitBinding.inflate(layoutInflater)
        setContentView(mBinder.root)

        init()
    }

    private fun init() {
        Log.i(TAG, "Initialising Application...")
        mBinder.loadingStatusText.text = getString(R.string.init_screen_emdk_check)

        initViewModel.isDataImported.observe(this, dataImportObserver)

        //Enable RESET_REPORTS_AFTER_TASK_COMPLETED Preference
        if (!mSharedPreferences.contains(AppConstants.RESET_REPORTS_AFTER_TASK_COMPLETED)) {
            mSharedPreferences.edit()
                .putBoolean(AppConstants.RESET_REPORTS_AFTER_TASK_COMPLETED, true).apply()
        }

        if (Environment.isExternalStorageManager()) {
            mBinder.loadingStatusText.text =
                getString(R.string.init_process_storage_permission_profile_success)
            createDWProfile()
        } else {
            mBinder.loadingStatusText.text =
                getString(R.string.init_process_storage_permission_profile_failed)
        }
    }

    private fun createDWProfile() {
        Log.i(TAG, "Creating DW Profile unless it doesn't exists already")

        val bMain = Bundle().apply {
            putString("PROFILE_NAME", "CsvBarcodeLookup")
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
        }

        val configApplicationList = Bundle().apply {
            putString("PACKAGE_NAME", packageName)
            putStringArray(
                "ACTIVITY_LIST",
                arrayOf("com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity")
            )
        }

        val intentModuleParamList = Bundle().apply {
            putString("intent_output_enabled", "true")
            putString("intent_action", AppConstants.DW_SCANNER_INTENT_ACTION)
            putInt("intent_delivery", 2)
        }

        val intentModule = Bundle().apply {
            putString("PLUGIN_NAME", "INTENT")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", intentModuleParamList)
        }

        val keystrokeModuleParamList = Bundle().apply {
            putString("keystroke_output_enabled", "false")
        }

        val keystrokeModule = Bundle().apply {
            putString("PLUGIN_NAME", "KEYSTROKE")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", keystrokeModuleParamList)
        }

        bMain.putParcelableArrayList("PLUGIN_CONFIG", arrayListOf(intentModule, keystrokeModule))
        bMain.putParcelableArray("APP_LIST", arrayOf(configApplicationList))

        val iSetConfig = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            setPackage("com.symbol.datawedge")
            putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain)
        }
        sendBroadcast(iSetConfig)

        initViewModel.importData()
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