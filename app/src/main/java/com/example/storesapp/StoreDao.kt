package com.example.storesapp

import androidx.room.*

@Dao
interface StoreDao {

    @Query("SELECT * FROM storeentity")
    fun getAllStores() : MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity) :Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)
}