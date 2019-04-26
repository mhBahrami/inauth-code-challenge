package com.inauth.codechallenge.data.source.local

import androidx.room.*
import com.inauth.codechallenge.data.AppsInfo

/**
 * Data Access Object for the apps_info table.
 */
@Dao
interface AppsInfoDao {
    /**
     * Select an apps info by id.
     *
     * @param id the apps info id.
     * @return the app info with id.
     */
    @Query("SELECT * FROM apps_info WHERE entry_id = :id") fun getAppsInfoById(id: Long): AppsInfo?

    /**
     * Insert an apps info in the database. If the apps info already exists, replace it.
     *
     * @param appsInfo the apps info to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertAppsInfo(appsInfo: AppsInfo)

    /**
     * Update an apps info.
     *
     * @param appsInfo the apps info to be updated
     * @return the number of apps info updated. This should always be 1.
     */
    @Update
    fun updateTask(appsInfo: AppsInfo): Int
}