package com.fpretell.demoperuapps.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [LocationEntity::class],
    version = 1)
abstract class LocationsDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        private const val USERS_DB = "Users.db"

        @Volatile
        private var INSTANCE: LocationsDatabase? = null

        fun getInstance(context: Context): LocationsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): LocationsDatabase {
            return Room.databaseBuilder(context.applicationContext,
                LocationsDatabase::class.java, USERS_DB)
                .fallbackToDestructiveMigration()
                .build()
        }

    }
}