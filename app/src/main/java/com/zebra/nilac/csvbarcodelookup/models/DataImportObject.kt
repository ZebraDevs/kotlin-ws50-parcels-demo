package com.zebra.nilac.csvbarcodelookup.models

data class DataImportObject(
    var errorMessage: String = "",

    var isCSVDataImported: Boolean = false,

    var isContainerLocationsImported: Boolean = false
)