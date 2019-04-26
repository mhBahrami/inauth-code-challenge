package com.inauth.codechallenge.data.source


interface AppDataSource {

    interface LoadPrettyJsonCallback {
        fun onDataNotAvailable()

        fun onLoadedPrettyJson(prettyJson: String)
    }

    interface GiphyDataSource {
        fun onSearch(query: String, callback: LoadPrettyJsonCallback)
    }

    interface LoadLocationInfoCallback {
        fun onTurnedOffLocation()

        fun onUnableToGetLocation()

        fun onLoadedLocationInfo(locationString: String, distInKmString: String)
    }

    interface LocationDataSource {
        fun onGetLastLocationInfo(callback: LoadLocationInfoCallback)
    }

    interface LocalDataSourceCallback {
        fun onDataNotAvailable()

        fun onLoadedAppsInfo(appsInfoString: String)
    }

    interface LocalDataSource {
        fun onGetAppsInfoById(id: Long, callback: LocalDataSourceCallback)

        fun onSaveAppsInfo(callback: LocalDataSourceCallback)
    }
}