package com.zebra.nilac.csvbarcodelookup.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zebra.nilac.csvbarcodelookup.models.Parcel
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel

@Dao
interface ReportDao {

    @Query("SELECT * FROM stored_parcels WHERE :container == CONTAINER")
    fun getParcelsByContainer(container: String): List<StoredParcel>

    @Query("SELECT * FROM stored_parcels")
    fun getParcels(): List<StoredParcel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNewStoredParcel(storedParcel: StoredParcel)

    @Query("SELECT * FROM stored_parcels WHERE :barcode == PARCEL_BARCODE")
    fun isParcelAlreadyStored(barcode: String): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM stored_parcels) == 0")
    fun isEmpty(): Boolean

    @Query("DELETE FROM stored_parcels")
    fun cleanAll()
}