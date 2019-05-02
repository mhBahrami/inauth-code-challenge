package com.inauth.codechallenge

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import com.google.android.gms.location.LocationServices
import com.inauth.codechallenge.data.source.AppRepository
import com.inauth.codechallenge.data.source.local.AppsLocalDataSource
import com.inauth.codechallenge.data.source.local.DeviceAppInfoDatabase
import com.inauth.codechallenge.data.source.local.CryptoApi
import com.inauth.codechallenge.data.source.remote.GiphyApiService
import com.inauth.codechallenge.data.source.remote.GiphyRemoteDataSource
import com.inauth.codechallenge.data.source.sensor.LocationDataSource
import com.inauth.codechallenge.util.AppExecutors

object Injection {

    fun provideAppRepository(context: Context): AppRepository {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val database = DeviceAppInfoDatabase.getInstance(context)
            val appPackageManager = context.packageManager
            return AppRepository.getInstance(
                GiphyRemoteDataSource.getInstance(AppExecutors(), provideGiphyApiService()),
                LocationDataSource.getInstance(
                    AppExecutors(),
                    LocationServices.getFusedLocationProviderClient(context.applicationContext)
                ),
                AppsLocalDataSource.getInstance(
                    AppExecutors(),
                    context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager,
                    context.getSystemService(Context.STORAGE_SERVICE) as StorageManager,
                    appPackageManager,
                    database.appsInfoDao(),
                    CryptoApi.getInstance()
                )
            )
        } else {
            return AppRepository.getInstance(
                GiphyRemoteDataSource.getInstance(AppExecutors(), provideGiphyApiService()),
                LocationDataSource.getInstance(AppExecutors(),
                    LocationServices.getFusedLocationProviderClient(context.applicationContext)))
        }
    }

    private fun provideGiphyApiService(): GiphyApiService {
        return GiphyApiService.getInstance()
    }
}