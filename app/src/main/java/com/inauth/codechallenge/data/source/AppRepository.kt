package com.inauth.codechallenge.data.source

class AppRepository(
    private val appRemoteDataSource: AppDataSource.GiphyDataSource,
    private val appSensorDataSource: AppDataSource.LocationDataSource,
    private val appLocalDataSource: AppDataSource.LocalDataSource?
) : AppDataSource.GiphyDataSource, AppDataSource.LocationDataSource, AppDataSource.LocalDataSource  {

    constructor(
        appRemoteDataSource: AppDataSource.GiphyDataSource,
        appSensorDataSource: AppDataSource.LocationDataSource
    ) : this(appRemoteDataSource, appSensorDataSource, null)

    override fun onSearch(query: String, callback: AppDataSource.LoadPrettyJsonCallback) {
        appRemoteDataSource.onSearch(query, callback)
    }

    override fun onGetLastLocationInfo(callback: AppDataSource.LoadLocationInfoCallback) {
        appSensorDataSource.onGetLastLocationInfo(callback)
    }

    override fun onGetAppsInfoById(id: Long, callback: AppDataSource.LocalDataSourceCallback) {
        if(appLocalDataSource!=null) appLocalDataSource.onGetAppsInfoById(id, callback)
        else callback.onDataNotAvailable()
    }

    override fun onSaveAppsInfo(callback: AppDataSource.LocalDataSourceCallback) {
        if(appLocalDataSource!=null) appLocalDataSource.onSaveAppsInfo(callback)
        else callback.onDataNotAvailable()
    }

    companion object {

        private var INSTANCE: AppRepository? = null

        /**
         * This is for Api levels >= 26
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param appRemoteDataSource the backend data source
         * *
         * @param appSensorDataSource the device's sensor data source
         * *
         * @param appLocalDataSource the device storage data source
         * *
         * @return the [AppRepository] instance
         */
        @JvmStatic fun getInstance(
            appRemoteDataSource: AppDataSource.GiphyDataSource,
            appSensorDataSource: AppDataSource.LocationDataSource,
            appLocalDataSource: AppDataSource.LocalDataSource) =
            INSTANCE
                ?: synchronized(AppRepository::class.java) {
                    INSTANCE
                        ?: AppRepository(appRemoteDataSource, appSensorDataSource, appLocalDataSource)
                            .also { INSTANCE = it }
                }

        /**
         * This is for Api levels =< 25
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param appRemoteDataSource the backend data source
         * *
         * @param appSensorDataSource the device's sensor data source
         * *
         * @return the [AppRepository] instance
         */
        @JvmStatic fun getInstance(
            appRemoteDataSource: AppDataSource.GiphyDataSource,
            appSensorDataSource: AppDataSource.LocationDataSource) =
            INSTANCE
                ?: synchronized(AppRepository::class.java) {
                    INSTANCE
                        ?: AppRepository(appRemoteDataSource, appSensorDataSource)
                            .also { INSTANCE = it }
                }


        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}