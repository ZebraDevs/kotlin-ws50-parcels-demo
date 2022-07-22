package com.zebra.nilac.csvbarcodelookup

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityMainBinding
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback

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
                }
            })
    }

    companion object {
        const val TAG = "InitActivity"
    }
}