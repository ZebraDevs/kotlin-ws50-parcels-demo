package com.zebra.nilac.csvbarcodelookup.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val productsDao = DefaultApplication.getInstance().getAppDatabaseInstance().productsDao

    val productResponse: MutableLiveData<Product?> by lazy {
        MutableLiveData<Product?>()
    }

    fun getProductByBarcode(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = productsDao.getProductByBarcode(barcode)
            productResponse.postValue(product)
        }
    }
}