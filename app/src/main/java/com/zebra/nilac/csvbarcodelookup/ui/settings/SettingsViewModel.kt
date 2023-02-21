package com.zebra.nilac.csvbarcodelookup.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.DataImportObject
import com.zebra.nilac.csvbarcodelookup.ui.init.InitViewModel
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor
import com.zebra.nilac.csvbarcodelookup.utils.ExcelDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao
    private val reportsDao = DefaultApplication.getInstance().getAppDatabaseInstance().reportsDao

    val isDataImported: MutableLiveData<DataImportObject> = MutableLiveData()
    val areParcelLocationsImported: MutableLiveData<DataImportObject> = MutableLiveData()
    val isReportSessionReset: MutableLiveData<Boolean> = MutableLiveData()

    fun importData() {
        Log.i(TAG, "Importing data from CSV File...")

        viewModelScope.launch(Dispatchers.IO) {
            //Clean up the Tables
            parcelsDao.cleanAll()
            reportsDao.cleanAll()

            val dataImportObject = DataImportObject()

            ExcelDataExtractor.extractDataFromFile(object : ExcelDataExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported data from CSV file...")

                    dataImportObject.isCSVDataImported = true
                    isDataImported.postValue(dataImportObject)
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

        viewModelScope.launch(Dispatchers.IO) {
            //Delete current locations & current report session
            reportsDao.cleanAll()
            DefaultApplication.getInstance().sharedPreferencesInstance!!.edit()
                .remove(AppConstants.CONTAINER_LOCATIONS_PREF).apply()

            ContainerLocationsExtractor.extractData(object :
                ContainerLocationsExtractor.CallBacks {
                override fun onFinished() {
                    Log.i(TAG, "Successfully imported container locations...")
                    areParcelLocationsImported.postValue(
                        DataImportObject(
                            "",
                            isCSVDataImported = true,
                            isContainerLocationsImported = true
                        )
                    )
                }

                override fun onFailed(errorMessage: String) {
                    Log.e(
                        TAG, "Failed to import containers locations:\n$errorMessage"
                    )

                    areParcelLocationsImported.postValue(
                        DataImportObject(
                            errorMessage,
                            isCSVDataImported = true,
                            isContainerLocationsImported = false
                        )
                    )
                }
            })
        }
    }

    fun resetReportSession() {
        Log.i(TAG, "Resetting current Report Session...")

        viewModelScope.launch(Dispatchers.IO) {
            reportsDao.cleanAll()
            isReportSessionReset.postValue(true)
        }
    }

    companion object {
        const val TAG = "SettingsViewModel"
    }
}