package com.inauth.codechallenge.data.source.local

import android.app.usage.StorageStatsManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import androidx.annotation.VisibleForTesting
import com.inauth.codechallenge.data.AppsInfo
import com.inauth.codechallenge.data.source.AppDataSource
import com.inauth.codechallenge.util.AppExecutors
import java.util.*


/**
 * Concrete implementation of a data source as a db.
 */
class AppsLocalDataSource private constructor(
    private val appExecutors: AppExecutors,
    private val appStorageStatsManager: StorageStatsManager,
    private val appStorageManager: StorageManager,
    private val appPackageManager: PackageManager,
    private val appsInfoDao: AppsInfoDao
) : AppDataSource.LocalDataSource {

    /**
     * Note: [LocalDataSourceCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun onGetAppsInfoById(id: Long, callback: AppDataSource.LocalDataSourceCallback) {
        appExecutors.diskIO.execute {
            val appsInfo = appsInfoDao.getAppsInfoById(id)
            appExecutors.mainThread.execute {
                if (appsInfo != null) {
                    callback.onLoadedAppsInfo(appsInfo.info)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun onSaveAppsInfo(callback: AppDataSource.LocalDataSourceCallback) {
        appExecutors.diskIO.execute {
            //get a list of installed apps.
            val packages = appPackageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            val stringBuilder = StringBuilder()
            for (packageInfo in packages) {
                var name = "Not Available"
                try {
                    val ai = appPackageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA)
                    name = appPackageManager.getApplicationLabel(ai) as String
                } catch (e: Exception) {
                    // ignore
                }

                if (Build.VERSION.SDK_INT >= 26) {
                    try {
                        val user = Process.myUserHandle()
                        val storageVolumes = appStorageManager.storageVolumes
                        for (storageVolume in storageVolumes) {
                            if (storageVolume.getDescription(null).contains("internal", true)) {
                                val uuidStr = storageVolume.uuid
                                val uuid =
                                    if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
                                try {
                                    val storageStats =
                                        appStorageStatsManager.queryStatsForPackage(uuid, packageInfo.packageName, user)
                                    val appSize = storageStats.appBytes
                                    val appDataSize = storageStats.dataBytes
                                    val appCacheSize = storageStats.cacheBytes
                                    stringBuilder.append("$name, ${packageInfo.packageName}, $appSize, $appDataSize, $appCacheSize\n")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                break
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
                else {
                    //FileDirSizeCalculator.getSize()
                }
            }

            val appsInfoString = stringBuilder.toString()
            if (appsInfoString.trim().isEmpty()) {
                appExecutors.mainThread.execute { callback.onDataNotAvailable() }
            } else {
                appsInfoDao.insertAppsInfo(AppsInfo(appsInfoString, appsInfoID))
                val savedInfo = appsInfoDao.getAppsInfoById(appsInfoID)
                appExecutors.mainThread.execute { callback.onLoadedAppsInfo(savedInfo?.info ?: "NULL") }
            }
        }
    }

    companion object {
        const val appsInfoID: Long = 21L
        private var INSTANCE: AppsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors,
                        appStorageStatsManager: StorageStatsManager,
                        appStorageManager: StorageManager,
                        appPackageManager: PackageManager,
                        appsInfoDao: AppsInfoDao): AppsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(AppsLocalDataSource::javaClass) {
                    INSTANCE = AppsLocalDataSource(
                        appExecutors,
                        appStorageStatsManager,
                        appStorageManager,
                        appPackageManager,
                        appsInfoDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

}