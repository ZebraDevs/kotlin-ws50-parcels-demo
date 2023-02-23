package com.zebra.nilac.csvbarcodelookup.ui.init

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao

    val isDataImported: MutableLiveData<DataImportObject> = MutableLiveData()

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

    companion object {
        const val TAG = "InitViewModel"
    }
}