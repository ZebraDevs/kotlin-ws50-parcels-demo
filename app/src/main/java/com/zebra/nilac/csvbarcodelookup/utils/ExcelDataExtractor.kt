package com.zebra.nilac.csvbarcodelookup.utils

import android.content.res.AssetManager
import android.util.Log
import com.opencsv.CSVParser
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.AppDatabase
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.ui.init.InitActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStream


object ExcelDataExtractor {

    private const val TAG = "ExcelDataExtractor"

    private var mCallBacks: CallBacks? = null
    private var database: AppDatabase? = null

    fun extractData(callBacks: CallBacks) {
        if (!AppConstants.CSV_FILE_PATH.exists()) {
            Log.w(
                TAG,
                "No CSV File found at specified destination and DB is empty, pouring data with samples info"
            )
            extractDataFromAssetsFile(callBacks)
        } else {
            Log.i(
                TAG,
                "CSV File was found, importing data from file"
            )
            extractDataFromFile(AppConstants.CSV_FILE_PATH, callBacks)
        }
    }

    private fun extractDataFromAssetsFile(callBacks: CallBacks) {
        val assetManager: AssetManager = DefaultApplication.getInstance().applicationContext.assets
        val inputStream = assetManager.open(AppConstants.CSV_FILE_NAME_S)
        val outputFile =
            File(
                DefaultApplication.getInstance().applicationContext.getExternalFilesDir(null)
                    .toString() + "/" + AppConstants.CSV_FILE_NAME_S
            )

        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }

        extractDataFromFile(outputFile, callBacks)
    }

    private fun extractDataFromFile(targetFile: File, callBacks: CallBacks) {
        mCallBacks = callBacks

        //Check if file exists
        if (!targetFile.exists()) {
            mCallBacks?.onFailed("File at specified path doesn't exist!")
            return
        }

        val separator: CSVParser = CSVParserBuilder()
            .withSeparator(';')
            .build()

        val reader: CSVReader = CSVReaderBuilder(FileReader(targetFile))
            .withCSVParser(separator)
            .build()

        database = DefaultApplication.getInstance().getAppDatabaseInstance()

        var nextCell: Array<String>?

        MainScope().launch(Dispatchers.IO) {
            //Clean all records first
            database?.parcelsDao!!.cleanAll()

            try {
                while (reader.readNext().also { nextCell = it } != null) {
                    val parcel = Parcel()

                    if (nextCell.isNullOrEmpty()) {
                        break
                    }

                    //If the row doesn't contain the barcode, skip it...
                    if (nextCell!![0].isEmpty()) {
                        continue
                    }

                    parcel.parcelBarcode = nextCell!![0]
                    parcel.assignedContainer = nextCell!![1]
                    pourDataToLocalDb(parcel)
                }
                mCallBacks?.onFinished()
            } catch (ex: Exception) {
                ex.printStackTrace()
                mCallBacks?.onFailed(ex.message.toString())
            }
        }
    }

    private fun pourDataToLocalDb(parcel: Parcel) {
        Log.d(TAG, "Pouring new parcel into DB with info:\n ${parcel.toString()}")

        //Pour extracted product into our DB
        database?.parcelsDao!!.insertNewParcel(parcel)
    }

    private fun copyAssetFileToData(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    interface CallBacks {
        fun onFinished()

        fun onFailed(errorMessage: String)
    }
}