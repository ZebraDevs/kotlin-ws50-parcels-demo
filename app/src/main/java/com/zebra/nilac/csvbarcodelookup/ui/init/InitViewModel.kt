package com.zebra.nilac.csvbarcodelookup.ui.init

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.R
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.ui.main.MainActivity
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class InitViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao

    val isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isDWProfileGenerated: MutableLiveData<Boolean> = MutableLiveData()
    val isDataImported: MutableLiveData<DataImportObject> = MutableLiveData()

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

    fun importData() {
        Log.i(TAG, "Importing data from CSV File...")

        viewModelScope.launch(Dispatchers.IO) {
            //Check if we have inserted data
            val isEmpty = parcelsDao.isEmpty()

            if (!isEmpty) {
                Log.i(TAG, "DB is already populated, skipping import...")
                importContainerLocations()
                return@launch
            }

            val dataImportObject = DataImportObject()

            ExcelDataExtractor.extractData(object : ExcelDataExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported data from CSV file...")

                    dataImportObject.isCSVDataImported = true
                    importContainerLocations()
                }

                override fun onFailed(errorMessage: String) {
                    Log.e(TAG, "Failed to load CSV data:\n$errorMessage")

                    dataImportObject.errorMessage = errorMessage
                    isDataImported.postValue(dataImportObject)
                }
            })
        }
    }

    fun importContainerLocations() {
        Log.i(TAG, "Importing container locations...")

        if (DefaultApplication.getInstance().sharedPreferencesInstance!!.contains(AppConstants.CONTAINER_LOCATIONS_PREF)) {
            Log.i(TAG, "Container locations were already imported, skipping...")
            isDataImported.postValue(
                DataImportObject(
                    "",
                    isCSVDataImported = true,
                    isContainerLocationsImported = true
                )
            )
            return
        }

        ContainerLocationsExtractor.extractData(object :
            ContainerLocationsExtractor.CallBacks {
            override fun onFinished() {
                Log.i(TAG, "Successfully imported container locations...")
                isDataImported.postValue(
                    DataImportObject(
                        "",
                        isCSVDataImported = true,
                        isContainerLocationsImported = true
                    )
                )
            }

            override fun onFailed(errorMessage: String) {
                Log.e(TAG, "Failed to import containers locations:\n$errorMessage")

                isDataImported.postValue(
                    DataImportObject(
                        errorMessage,
                        isCSVDataImported = true,
                        isContainerLocationsImported = false
                    )
                )
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