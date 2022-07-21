package com.zebra.nilac.csvbarcodelookup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityInitBinding
import com.zebra.nilac.csvbarcodelookup.databinding.ActivityMainBinding
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack

class InitActivity : AppCompatActivity() {

    private lateinit var mBinder: ActivityInitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityInitBinding.inflate(layoutInflater)

        setContentView(mBinder.root)

        initEMDKManager()
    }

    fun initEMDKManager() {
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

                //TODO
            }
        })
    }

    companion object {
        const val TAG = "InitActivity"
    }
}