package com.zebra.nilac.csvbarcodelookup.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zebra.nilac.csvbarcodelookup.models.Product

@Dao
interface ProductsDao {

    @Query("SELECT * FROM products WHERE :barcode == COLLO_EAN")
    fun getProductByBarcode(barcode: String): Product?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNewProduct(product: Product)

    @Query("DELETE FROM products")
    fun cleanAll()
}