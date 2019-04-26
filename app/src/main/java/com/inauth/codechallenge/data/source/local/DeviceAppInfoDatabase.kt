package com.inauth.codechallenge.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inauth.codechallenge.data.AppsInfo

/**
 * The Room Database that contains the AppsInfo table.
 */
@Database(entities = [AppsInfo::class], version = 1, exportSchema = false)
abstract class DeviceAppInfoDatabase : RoomDatabase() {

    abstract fun appsInfoDao(): AppsInfoDao

    companion object {

        private var INSTANCE: DeviceAppInfoDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): DeviceAppInfoDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        DeviceAppInfoDatabase::class.java, "AppsInfo.db")
                        .build()
                }
                return INSTANCE!!
            }
        }
    }

}