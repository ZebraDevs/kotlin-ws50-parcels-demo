package com.zebra.nilac.csvbarcodelookup

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zebra.nilac.csvbarcodelookup.dao.ParcelDao
import com.zebra.nilac.csvbarcodelookup.dao.ReportDao
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel

@Database(version = 4, entities = [Parcel::class, StoredParcel::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val parcelsDao: ParcelDao

    abstract val reportsDao: ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "parcels_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}