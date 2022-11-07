package com.zebra.nilac.csvbarcodelookup

import android.os.Environment
import java.io.File

object AppConstants {

    const val SHARED_PREFERENCES_CONSTANT_NAME = BuildConfig.APPLICATION_ID + ".data"

    const val DW_PROFILE_NAME = "CsvBarcodeLookup"
    const val FOLDER_NAME = "csv-barcode-lookup"

    const val CSV_FILE_NAME = "parcels_list.csv"
    const val CSV_FILE_NAME_S = "parcels_list_sample.csv"
    val CSV_FILE_PATH =
        File(Environment.getExternalStorageDirectory().absolutePath + "/$FOLDER_NAME/$CSV_FILE_NAME")

    const val CONTAINER_LOCATIONS_FILE_NAME = "container_locations.json"
    const val CONTAINER_LOCATIONS_FILE_NAME_S = "container_locations_sample.json"
    val CONTAINER_LOCATIONS_FILE_PATH =
        File(Environment.getExternalStorageDirectory().absolutePath + "/$FOLDER_NAME/$CONTAINER_LOCATIONS_FILE_NAME")

    const val CONTAINER_LOCATIONS_PREF = "com.zebra.nilac.csvbarcodelookup.CONTAINER_LOCATIONS_PREF"

    const val DW_SCANNER_INTENT_ACTION = "com.zebra.nilac.csvbarcodelookup.SCANNER"
    const val DW_DATA_STRING_TAG = "com.symbol.datawedge.data_string"
}
