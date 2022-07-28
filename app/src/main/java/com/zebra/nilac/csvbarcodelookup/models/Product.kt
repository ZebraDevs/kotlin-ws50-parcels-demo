package com.zebra.nilac.csvbarcodelookup.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Entity(tableName = "products")
@Parcelize
data class Product(

    @ColumnInfo(name = "COLLO_EAN")
    var barcode: String = "",

    @ColumnInfo(name = "Vulpad_naam")
    var name: String = "",

    @ColumnInfo(name = "Vulpad_nummer")
    var number: Int = 0,

    @ColumnInfo(name = "OMSCHRIJVING")
    var description: String = "",
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    override fun toString(): String {
        return """Name: ${name} Description: ${description} Barcode: $barcode  Number: $number"""
    }
}