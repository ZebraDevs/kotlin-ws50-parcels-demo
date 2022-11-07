package com.zebra.nilac.csvbarcodelookup.utils

import android.content.res.AssetManager
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import com.zebra.nilac.csvbarcodelookup.AppConstants
import com.zebra.nilac.csvbarcodelookup.DefaultApplication
import okio.buffer
import okio.source
import java.io.FileInputStream
import java.io.FileOutputStream

object ContainerLocationsExtractor {

    private const val TAG = "ContainerLocationsExtractor"

    private val mMoshiInstance = DefaultApplication.getInstance().moshiInstance!!
    private val mSharedPreferenceInstance =
        DefaultApplication.getInstance().sharedPreferencesInstance!!

    private var locationsAdapter: JsonAdapter<List<String>> = mMoshiInstance.adapter(
        Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
    )

    fun extractData(callBacks: CallBacks) {
        if (!AppConstants.CONTAINER_LOCATIONS_FILE_PATH.exists()) {
            Log.w(
                TAG,
                "No JSON File found at specified destination, importing container locations from samples"
            )
            extractDataFromAssetsFile(callBacks)
        } else {
            Log.i(
                TAG,
                "JSON File was found, importing data from specified location"
            )
            extractDataFromFile(callBacks)
        }
    }

    private fun extractDataFromAssetsFile(callBacks: CallBacks) {
        val assetManager: AssetManager = DefaultApplication.getInstance().applicationContext.assets

        val inputStream = assetManager.open(AppConstants.CONTAINER_LOCATIONS_FILE_NAME_S)
        val containerLocationsList = locationsAdapter.fromJson(inputStream.source().buffer())!!

        mSharedPreferenceInstance.edit().apply {
            putString(
                AppConstants.CONTAINER_LOCATIONS_PREF,
                locationsAdapter.toJson(containerLocationsList)
            )
        }.apply()

        callBacks.onFinished()
    }

    private fun extractDataFromFile(callBacks: CallBacks) {
        try {
            val fis = FileInputStream(AppConstants.CONTAINER_LOCATIONS_FILE_PATH)
            val containerLocationsList = locationsAdapter.fromJson(fis.source().buffer())!!
            mSharedPreferenceInstance.edit().apply {
                putString(
                    AppConstants.CONTAINER_LOCATIONS_PREF,
                    locationsAdapter.toJson(containerLocationsList)
                )
            }.apply()
            callBacks.onFinished()
        } catch (ex: Exception) {
            ex.printStackTrace()
            callBacks.onFailed(ex.message!!)
        }
    }

    fun getLocations(): List<String> {
        return locationsAdapter.fromJson(
            mSharedPreferenceInstance.getString(
                AppConstants.CONTAINER_LOCATIONS_PREF,
                ""
            )!!
        )!!
    }

    interface CallBacks {
        fun onFinished()

        fun onFailed(errorMessage: String)
    }
}