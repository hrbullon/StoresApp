package com.example.storesapp

import android.app.Application
import androidx.room.Database
import androidx.room.Room

class StoreApplication: Application() {
    companion object {
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val DATABASE_STORE_NAME = "StoreDatabase"
        database = Room.databaseBuilder(this,StoreDatabase::class.java,DATABASE_STORE_NAME).build()
    }
}