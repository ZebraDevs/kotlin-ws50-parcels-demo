package com.zebra.nilac.csvbarcodelookup.utils

import android.util.Log
import com.opencsv.CSVParser
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.zebra.nilac.csvbarcodelookup.AppDatabase
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import com.zebra.nilac.csvbarcodelookup.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader
import java.lang.Exception


object ExcelDataExtractor {

    private const val TAG = "ExcelDataExtractor"

    private var mCallBacks: CallBacks? = null
    private var database: AppDatabase? = null

    fun extractDataFromFile(targetFile: File, callBacks: CallBacks) {
        mCallBacks = callBacks

        //Check if file exists
        if (!targetFile.exists()) {
            mCallBacks?.onFailed("File at specified path doesn't exist!")
            return
        }

        val separator: CSVParser = CSVParserBuilder()
            .withSeparator(',')
            .build()

        val reader: CSVReader = CSVReaderBuilder(FileReader(targetFile))
            .withSkipLines(1)
            .withCSVParser(separator)
            .build()

        database = DefaultApplication.getInstance().getAppDatabaseInstance()

        var nextCell: Array<String>?

        MainScope().launch(Dispatchers.IO) {
            //Clean all records first
            database?.productsDao!!.cleanAll()

            try {
                while (reader.readNext().also { nextCell = it } != null) {
                    val product: Product = Product()

                    if (nextCell.isNullOrEmpty()) {
                        break
                    }

                    //If the row doesn't contain the barcode, skip it...
                    if (nextCell!![3].isEmpty()) {
                        continue
                    }

                    product.description = nextCell!![2]
                    product.barcode = nextCell!![3]
                    product.name = nextCell!![5]
                    product.number = try {
                        nextCell!![6].toInt()
                    } catch (ex: NumberFormatException) {
                        0
                    }
                    pourDataToLocalDb(product)
                }
                mCallBacks?.onFinished()
            } catch (ex: Exception) {
                ex.printStackTrace()
                mCallBacks?.onFailed(ex.message.toString())
            }
        }
    }

    private fun pourDataToLocalDb(product: Product) {
        Log.d(TAG, "Pouring new product into DB with info:\n ${product.toString()}")

        //Pour extracted product into our DB
        database?.productsDao!!.insertNewProduct(product)
    }

    interface CallBacks {
        fun onFinished()

        fun onFailed(errorMessage: String)
    }
}