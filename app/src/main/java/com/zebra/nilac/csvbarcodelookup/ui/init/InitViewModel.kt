package com.zebra.nilac.csvbarcodelookup.ui.init

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao

    val isDbEmpty: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isDWProfileGenerated: MutableLiveData<Boolean> = MutableLiveData()
    val isDataImported: MutableLiveData<Boolean> = MutableLiveData()

    fun grantManageExternalStoragePermission() {
        Log.i(TAG, "Granting External Storage Permission through MX")

        if (!EMDKLoader.getInstance().isManagerInit()) {
            initEMDKManager()
            return
        }

        ProfileLoader().processProfile(
            "AccessManager",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here...
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process External Storage Permission!")
                    isPermissionGranted.postValue(false)
                }

                override fun onProfileLoaded() {
                    isPermissionGranted.postValue(true)
                }
            })
    }

    fun createDWProfile() {
        ProfileLoader().processProfile(
            AppConstants.DW_PROFILE_NAME,
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here
                }

                override fun onProfileLoadFailed(message: String) {
                    isDWProfileGenerated.postValue(false)
                }

                override fun onProfileLoaded() {
                    isDWProfileGenerated.postValue(true)
                }
            })
    }

    fun isDbEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val isEmpty = parcelsDao.isEmpty()
            isDbEmpty.postValue(isEmpty)
        }
    }

    fun importCsvData() {
        Log.i(TAG, "Importing data from CSV File...")

        ExcelDataExtractor.extractDataFromFile(
            AppConstants.CSV_FILE_PATH, object : ExcelDataExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported data from CSV file...")
                    isDataImported.postValue(true)

                    //Delete the imported file
                    AppConstants.CSV_FILE_PATH.delete()
                }

                override fun onFailed(errorMessage: String) {
                    Log.e(TAG, "Failed to load CSV data:\n$errorMessage")
                    isDataImported.postValue(false)
                }
            })
    }

    private fun initEMDKManager() {
        //Initialising EMDK First...
        Log.i(TAG, "Initialising EMDK Manager")

        EMDKLoader.getInstance().initEMDKManager(
            DefaultApplication.getInstance().applicationContext,
            object : EMDKManagerInitCallBack {
                override fun onFailed(message: String) {
                    Log.e(TAG, "Failed to initialise EMDK Manager")
                    isPermissionGranted.postValue(false)
                }

                override fun onSuccess() {
                    Log.i(TAG, "EMDK Manager was successfully initialised")

                    grantManageExternalStoragePermission()
                }
            })
    }

    companion object {
        const val TAG = "InitViewModel"
    }
}