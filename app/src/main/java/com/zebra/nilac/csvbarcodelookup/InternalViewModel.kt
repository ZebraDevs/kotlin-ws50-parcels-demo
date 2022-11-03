package com.zebra.nilac.csvbarcodelookup

import androidx.lifecycle.*
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternalViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao

    val parcelResponse: MutableLiveData<Parcel?> by lazy {
        MutableLiveData<Parcel?>()
    }

    val barcodeResponse: MutableLiveData<Event<String>> = MutableLiveData()

    fun getParcelByBarcode(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = parcelsDao.getParcelByBarcode(barcode)
            parcelResponse.postValue(product)
        }
    }
}