package com.zebra.nilac.csvbarcodelookup.ui.reports

import androidx.lifecycle.*
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Event
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer
import com.zebra.nilac.csvbarcodelookup.utils.ContainerLocationsExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportsSummaryViewModel : ViewModel() {

    private val reportsDao = DefaultApplication.getInstance().getAppDatabaseInstance().reportsDao

    val containerResponse: MutableLiveData<List<ReportContainer>> by lazy {
        MutableLiveData<List<ReportContainer>>()
    }

    fun getReportContainersParcelsCount() {
        val locations = ContainerLocationsExtractor.getLocations()

        viewModelScope.launch(Dispatchers.IO) {
            val parcels = reportsDao.getParcels()
            val reportLocations: MutableList<ReportContainer> = ArrayList()

            for (location in locations) {
                val reportLocation = ReportContainer(location, 0)
                for (parcel in parcels) {
                    if (parcel.assignedContainer == location) {
                        reportLocation.parcelsCount++
                    }
                }
                reportLocations.add(reportLocation)
            }
            containerResponse.postValue(reportLocations)
        }
    }
}