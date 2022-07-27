package com.zebra.nilac.csvbarcodelookup

import android.app.Application

class DefaultApplication : Application() {

    init {
        INSTANCE = this
    }

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(getInstance())
    }

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