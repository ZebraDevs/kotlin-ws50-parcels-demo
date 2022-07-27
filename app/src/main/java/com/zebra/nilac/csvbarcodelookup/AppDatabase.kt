package com.zebra.nilac.csvbarcodelookup

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zebra.nilac.csvbarcodelookup.dao.ProductsDao
import com.zebra.nilac.csvbarcodelookup.models.Product

@Database(version = 1, entities = [Product::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val productsDao: ProductsDao

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
                        "products_db"
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