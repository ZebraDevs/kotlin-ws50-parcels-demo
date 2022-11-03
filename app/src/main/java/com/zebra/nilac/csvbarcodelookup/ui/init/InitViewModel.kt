package com.zebra.nilac.csvbarcodelookup.ui.init

import androidx.lifecycle.*
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {

    private val parcelsDao = DefaultApplication.getInstance().getAppDatabaseInstance().parcelsDao

    val isDbEmpty: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun isDbEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val isEmpty = parcelsDao.isEmpty()
            isDbEmpty.postValue(isEmpty)
        }
    }
}