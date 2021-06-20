package com.freelansoft.mywork.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.freelansoft.mywork.service.PlantService
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private var _plantService: PlantService = PlantService(application)
    private val locationLiveData = LocationLiveData(application)
    internal fun getLocationLiveData() = locationLiveData

    init {
        fetchPlants("e")
    }

    fun fetchPlants(plantName: String) {
        viewModelScope.launch{
            _plantService.fetchPlants(plantName)
        }
    }

    internal var plantService : PlantService
        get() {return _plantService}
        set(value) {_plantService = value}
}