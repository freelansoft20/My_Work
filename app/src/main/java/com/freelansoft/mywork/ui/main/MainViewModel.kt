package com.freelansoft.mywork.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freelansoft.mywork.dto.Plant
import com.freelansoft.mywork.service.PlantService

class MainViewModel : ViewModel() {
    var plants: MutableLiveData<ArrayList<Plant>> = MutableLiveData<ArrayList<Plant>>()
    var plantService: PlantService = PlantService()

    init {
        fetchPlants("e")
    }

    fun fetchPlants(plantName: String) {
        plants = plantService.fetchPlants(plantName)
    }
    // TODO: Implement the ViewModel
}