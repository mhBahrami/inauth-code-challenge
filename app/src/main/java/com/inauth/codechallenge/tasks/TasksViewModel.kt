package com.inauth.codechallenge.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inauth.codechallenge.Event
import com.inauth.codechallenge.R
import com.inauth.codechallenge.data.source.AppDataSource
import com.inauth.codechallenge.data.source.AppRepository
import com.inauth.codechallenge.data.source.remote.GiphyApiService

/**
 * Exposes the data to be used in the gif list screen.
 *
 *
 * [BaseObservable] implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a [Bindable] annotation to the property's
 * getter method.
 */
class TasksViewModel(
    private val appRepository: AppRepository
) : ViewModel(), PrettyJsonProvider, LocationInfoProvider , ApplicationsInfoProvider{

    private val _prettyJsonVisibility = MutableLiveData<Boolean>()
    val prettyJsonVisibility: LiveData<Boolean>
        get() = _prettyJsonVisibility

    private val _prettyJsonString = MutableLiveData<String>().apply { value = "" }
    val prettyJsonString: LiveData<String>
        get() = _prettyJsonString

    private val _locationViewVisibility = MutableLiveData<Boolean>()
    val locationViewVisibility: LiveData<Boolean>
        get() = _locationViewVisibility

    private val _locationValue = MutableLiveData<String>()
    val locationValue: LiveData<String>
        get() = _locationValue

    private val _distanceValue = MutableLiveData<String>()
    val distanceValue: LiveData<String>
        get() = _distanceValue

    private val _appsInfoVisibility = MutableLiveData<Boolean>()
    val appsInfoVisibility: LiveData<Boolean>
        get() = _appsInfoVisibility

    private val _appsInfoString = MutableLiveData<String>()
    val appsInfoString: LiveData<String>
        get() = _appsInfoString

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _noResultLabelRes = MutableLiveData<Int>()
    val noResultLabelRes: LiveData<Int>
        get() = _noResultLabelRes

    private val _noResultIconRes = MutableLiveData<Int>()
    val noResultIconRes: LiveData<Int>
        get() = _noResultIconRes

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _emptyVisibility = MutableLiveData<Boolean>().apply { value = true }
    val emptyVisibility: LiveData<Boolean>
        get() = _emptyVisibility

    val buttonEnable = MediatorLiveData<Boolean>().apply { value = true }


    init {
        // Set initial state
        setupEmptyView(R.drawable.ic_primary, R.string.primary_message)
    }

    override fun search() {
        changeVisibility(VisibilityType.NONE)
        replacePrettyJson("")
        isLoading(true)

        _snackbarText.value = Event(R.string.new_request_pretty_json)

        appRepository.onSearch(GiphyApiService.DEFAULT_QUERY, object : AppDataSource.LoadPrettyJsonCallback {
            override fun onDataNotAvailable() {
                setupEmptyView(R.drawable.ic_search, R.string.no_result)
                replacePrettyJson("")

                isLoading(false)
                changeVisibility(VisibilityType.EMPTY_VIEW)
            }

            override fun onLoadedPrettyJson(prettyJson: String) {
                replacePrettyJson(prettyJson)

                isLoading(false)
                changeVisibility(VisibilityType.PRETTY_JSON_VIEW)
            }
        })

    }

    override fun getLastLocationInfo() {
        changeVisibility(VisibilityType.NONE)
        replacePrettyJson("")
        isLoading(true)

        _snackbarText.value = Event(R.string.new_request_location)

        appRepository.onGetLastLocationInfo(object: AppDataSource.LoadLocationInfoCallback {
            override fun onTurnedOffLocation() {
                setupEmptyView(R.drawable.ic_location_failed, R.string.location_off)

                isLoading(false)
                changeVisibility(VisibilityType.EMPTY_VIEW)
            }

            override fun onUnableToGetLocation() {
                setupEmptyView(R.drawable.ic_location_failed, R.string.no_result_location)

                isLoading(false)
                changeVisibility(VisibilityType.EMPTY_VIEW)
            }

            override fun onLoadedLocationInfo(locationString: String, distInKmString: String) {
                _locationValue.value = locationString
                _distanceValue.value = distInKmString

                isLoading(false)
                changeVisibility(VisibilityType.LOCATION_INFO_VIEW)
            }
        })
    }

    override fun getApplicationsInfo() {
        changeVisibility(VisibilityType.NONE)
        replacePrettyJson("")
        isLoading(true)

        _snackbarText.value = Event(R.string.new_request_apps_info)

        appRepository.onSaveAppsInfo(object : AppDataSource.LocalDataSourceCallback {
            override fun onDataNotAvailable() {
                setupEmptyView(R.drawable.ic_storage, R.string.no_apps_info)

                isLoading(false)
                changeVisibility(VisibilityType.EMPTY_VIEW)
            }

            override fun onLoadedAppsInfo(appsInfoString: String) {
                _appsInfoString.value = appsInfoString
                isLoading(false)
                changeVisibility(VisibilityType.APPLICATION_INFO_VIEW)
            }
        })
    }

    /**
     * Helper methods
     */
    private fun changeVisibility(type: VisibilityType) {
        _emptyVisibility.value = type == VisibilityType.EMPTY_VIEW
        _prettyJsonVisibility.value = type == VisibilityType.PRETTY_JSON_VIEW
        _locationViewVisibility.value = type == VisibilityType.LOCATION_INFO_VIEW
        _appsInfoVisibility.value = type == VisibilityType.APPLICATION_INFO_VIEW
    }

    fun isLoading(loading: Boolean) {
        _dataLoading.value = loading
        buttonEnable.value = !loading
    }

    private fun replacePrettyJson(prettyJson: String) {
        _prettyJsonString.value = prettyJson
    }

    private fun setupEmptyView(@DrawableRes noGifIconDrawable: Int,
                               @StringRes noGifLabelString: Int) {
        _noResultIconRes.value = noGifIconDrawable
        _noResultLabelRes.value = noGifLabelString
    }
}