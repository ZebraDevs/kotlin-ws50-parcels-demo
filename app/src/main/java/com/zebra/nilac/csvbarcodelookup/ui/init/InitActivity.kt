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
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.csvbarcodelookup.*
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import java.io.File

class InitActivity : AppCompatActivity() {

    private val CSVFile =
        File(Environment.getExternalStorageDirectory().absolutePath + "/${AppConstants.FOLDER_NAME}/${AppConstants.FILE_NAME_PREFIX}.csv")

    private val initViewModel: InitViewModel by viewModels()
    private lateinit var mBinder: ActivityInitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityInitBinding.inflate(layoutInflater)

        initViewModel.isDbEmpty.observe(this, databaseObserver)

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
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here...
                }

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

                        //Send Broadcast and activate the DataMatrix Decoder, it takes much more time verifying it than just send the broadcast directly
                        enableDataMatrixDecoder()

                        checkDb()
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
                        add("BARCODE")
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
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here
                }

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
                    checkDb()
                }
            })
    }

    private fun enableDataMatrixDecoder() {
        val bMain = Bundle().apply {
            putString("PROFILE_NAME", "CsvBarcodeLookup")
            putString("CONFIG_MODE", "UPDATE")
        }

        val barcodeParams = Bundle().apply {
            putString("decoder_datamatrix", "true")
        }

        val bParams = Bundle().apply {
            putParcelableArrayList("DECODERS", arrayListOf(barcodeParams))
        }

        val bConfig = Bundle().apply {
            putString("PLUGIN_NAME", "BARCODE")
            putString("RESET_CONFIG", "true")
            putBundle("PARAM_LIST", bParams)
        }

        bMain.putBundle("PLUGIN_CONFIG", bConfig)
        val i = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain)
        }
        this.sendBroadcast(i)
    }

    private val databaseObserver = Observer<Boolean> { isEmpty ->
        if (!CSVFile.exists() && isEmpty) {
            Log.w(
                TAG,
                "No CSV File found at specified destination and DB is empty, waiting for user input"
            )

            mBinder.loadingStatusText.text =
                getString(R.string.init_process_load_csv_data_not_found)

            mBinder.csvImportRetryButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    visibility = View.GONE
                    checkDb()
                }
            }
        } else if (!CSVFile.exists()) {
            Log.i(
                TAG,
                "No CSV File found at specified destination, assuming there's nothing to update!"
            )
            finish()
            startActivity(Intent(this@InitActivity, MainActivity::class.java))
        } else {
            importCsvData()
        }
    }

    private fun checkDb() {
        initViewModel.isDbEmpty()
    }

    private fun importCsvData() {
        Log.i(TAG, "Importing data from CSV File...")
        runOnUiThread {
            mBinder.loadingStatusText.text =
                getString(R.string.init_process_load_csv_data)
        }

        ExcelDataExtractor.extractDataFromFile(
            CSVFile, object : ExcelDataExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported data from CSV file...")
                    runOnUiThread {
                        mBinder.loadingStatusText.text =
                            getString(R.string.init_process_load_csv_data_success)
                    }

                    //Delete the imported file
                    CSVFile.delete()

                    finish()
                    startActivity(Intent(this@InitActivity, MainActivity::class.java))
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