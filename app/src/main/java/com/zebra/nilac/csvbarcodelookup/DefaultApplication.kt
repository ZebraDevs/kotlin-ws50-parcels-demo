package com.zebra.nilac.csvbarcodelookup

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.zebra.nilac.csvbarcodelookup.adapters.moshi.MoshiKotlinJsonAdaptersFactory
import com.zebra.nilac.csvbarcodelookup.adapters.moshi.MoshiStandardJsonAdapters
import java.util.*

class DefaultApplication : Application() {

    init {
        INSTANCE = this
    }

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(getInstance())
    }

    var moshiInstance: Moshi? = null
        get() {
            if (field == null) {
                field = Moshi.Builder()
                    .add(MoshiKotlinJsonAdaptersFactory())
                    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                    .add(MoshiStandardJsonAdapters.FACTORY)
                    .build()
            }
            return field!!
        }
        private set

    var sharedPreferencesInstance: SharedPreferences? = null
        get() {
            if (field == null) {
                field = EncryptedSharedPreferences.create(
                    AppConstants.SHARED_PREFERENCES_CONSTANT_NAME,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    getInstance().applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
            return field
        }
        private set

    fun getAppDatabaseInstance(): AppDatabase {
        return appDatabase
    }

    companion object {
        @Volatile
        private var INSTANCE: DefaultApplication? = null

        fun getInstance(): DefaultApplication {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = DefaultApplication()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}