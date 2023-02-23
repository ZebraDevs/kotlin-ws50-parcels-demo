package com.zebra.nilac.csvbarcodelookup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel
import com.zebra.nilac.csvbarcodelookup.ui.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternalViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao
    private val reportsDao = DefaultApplication.getInstance().getAppDatabaseInstance().reportsDao

    val parcelResponse: MutableLiveData<Parcel?> by lazy {
        MutableLiveData<Parcel?>()
    }

    val barcodeResponse: MutableLiveData<Event<String>> = MutableLiveData()
    val parcelReportInsertionResponse: MutableLiveData<Event<Parcel>> = MutableLiveData()

    val parcelsAndStoredCount: MutableLiveData<Event<Array<Int>>> = MutableLiveData()

    fun getParcelsAndStoredCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = parcelsDao.getParcelsTotalCount()
            val storedCount = reportsDao.getParcelsTotalCount()
            parcelsAndStoredCount.postValue(Event(arrayOf(storedCount, count)))
        }
    }

    fun getParcelByBarcode(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = parcelsDao.getParcelByBarcode(barcode)
            parcelResponse.postValue(product)
        }
    }

    fun insertNewParcel(parcel: Parcel) {
        viewModelScope.launch(Dispatchers.IO) {
            val isParcelAlreadyStored = reportsDao.isParcelAlreadyStored(parcel.parcelBarcode)

            if (isParcelAlreadyStored) {
                parcelReportInsertionResponse.postValue(Event(Parcel()))
                return@launch
            }

            reportsDao.insertNewStoredParcel(
                StoredParcel(
                    parcel.parcelBarcode,
                    parcel.assignedContainer
                )
            )
            parcelReportInsertionResponse.postValue(Event(parcel))
        }
    }

    fun resetReportsWithoutConfirmation() {
        Log.i(SettingsViewModel.TAG, "Resetting current Report Session...")

        viewModelScope.launch(Dispatchers.IO) {
            reportsDao.cleanAll()
        }
    }
}