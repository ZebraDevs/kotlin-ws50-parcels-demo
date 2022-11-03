package com.zebra.nilac.csvbarcodelookup.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Entity(tableName = "parcels")
@Parcelize
data class Parcel(

    @ColumnInfo(name = "PARCEL_BARCODE")
    var parcelBarcode: String = "",

    @ColumnInfo(name = "CONTAINER")
    var assignedContainer: String = "",
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    override fun toString(): String {
        return """Parcel Barcode: $parcelBarcode Assigned Container: $assignedContainer"""
    }
}