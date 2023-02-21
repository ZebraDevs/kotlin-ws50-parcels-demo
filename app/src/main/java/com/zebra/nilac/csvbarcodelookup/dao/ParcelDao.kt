package com.zebra.nilac.csvbarcodelookup.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zebra.nilac.csvbarcodelookup.models.Parcel

@Dao
interface ParcelDao {

    @Query("SELECT * FROM parcels WHERE :barcode == PARCEL_BARCODE")
    fun getParcelByBarcode(barcode: String): Parcel?

    @Query("SELECT COUNT(*) FROM parcels")
    fun getParcelsTotalCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNewParcel(parcel: Parcel)

    @Query("SELECT (SELECT COUNT(*) FROM parcels) == 0")
    fun isEmpty(): Boolean

    @Query("DELETE FROM parcels")
    fun cleanAll()
}