package com.freelansoft.mywork.ui.main

import android.content.ContentValues
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
    private var _specimens: MutableLiveData<ArrayList<Specimen>> = MutableLiveData<ArrayList<Specimen>>()


    init {
        fetchPlants("e")
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToSpecimens()
    }

    private fun listenToSpecimens() {
        firestore.collection("specimens").addSnapshotListener {
            snapshot, e ->
            // if there is an exception we want to skip.
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                // now, we have a populated shapshot
                val allSpecimens = ArrayList<Specimen>()
                val documents = snapshot.documents
                documents.forEach {

                    val specimen = it.toObject(Specimen::class.java)
                    if (specimen != null) {
                        specimen.specimenId = it.id
                        allSpecimens.add(specimen!!)
                    }
                }
                _specimens.value = allSpecimens
            }
        }
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

    internal var specimens: MutableLiveData<ArrayList<Specimen>>
        get() { return _specimens}
        set(value) {_specimens = value}
}