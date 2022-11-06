package com.zebra.nilac.csvbarcodelookup

import android.os.Environment
import java.io.File

object AppConstants {

    const val DW_PROFILE_NAME = "CsvBarcodeLookup"

    const val FILE_NAME_PREFIX = "excel-to-load"
    const val FOLDER_NAME = "csv-barcode-lookup"

    val CSV_FILE_PATH =
        File(Environment.getExternalStorageDirectory().absolutePath + "/$FOLDER_NAME/$FILE_NAME_PREFIX.csv")

    const val DW_SCANNER_INTENT_ACTION = "com.zebra.nilac.csvbarcodelookup.SCANNER"
    const val DW_DATA_STRING_TAG = "com.symbol.datawedge.data_string"
}
