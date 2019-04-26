package com.inauth.codechallenge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Immutable model class for a AppsInfo. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param info  device's apps' respective code sizes, data sizes, and cache sizes on the device.
 *              Store this information in a String delimited by commas
 * @param id    id of the apps info
 */
@Entity(tableName = "apps_info")
data class AppsInfo @JvmOverloads constructor(
    @ColumnInfo(name = "info") var info: String = "",
    @PrimaryKey @ColumnInfo(name = "entry_id") var id: Long = 1L
)