package com.freelansoft.mywork.service

import androidx.lifecycle.MutableLiveData
import com.freelansoft.mywork.dto.Plant

class PlantService {

    fun fetchPlants(plantName: String): MutableLiveData<ArrayList<Plant>> {
        return MutableLiveData<ArrayList<Plant>>()
    }
}