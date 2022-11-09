package com.zebra.nilac.csvbarcodelookup

import androidx.lifecycle.*
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel
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
}