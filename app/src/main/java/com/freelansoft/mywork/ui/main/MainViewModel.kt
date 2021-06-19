package com.freelansoft.mywork.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freelansoft.mywork.dto.Plant
import com.freelansoft.mywork.dto.Specimen
import com.freelansoft.mywork.service.PlantService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainViewModel : ViewModel() {
    private var _plants: MutableLiveData<ArrayList<Plant>> = MutableLiveData<ArrayList<Plant>>()
    private var plantService: PlantService = PlantService()
    private lateinit var firestore : FirebaseFirestore


    init {
        fetchPlants("e")
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
//        listenToSpecimens()
    }


    fun fetchPlants(plantName: String) {
        _plants = plantService.fetchPlants(plantName)
    }

    fun save(specimen: Specimen) {
        val document = firestore.collection("specimens").document()
        specimen.specimenId = document.id
        val set = document.set(specimen)
                set.addOnSuccessListener {
                    Log.d("Firebase", "Document saved")
                }
                set.addOnFailureListener {
                    Log.d("Firebase", "Save failed")
                }
    }

    internal var plants: MutableLiveData<ArrayList<Plant>>
        get() {return _plants}
        set(value) {_plants = value}
}