package com.freelansoft.mywork.service

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.freelansoft.mywork.RetrofitClientInstance
import com.freelansoft.mywork.dao.ILocalPlantDAO
import com.freelansoft.mywork.dao.IPlantDAO
import com.freelansoft.mywork.dao.PlantDatabase
import com.freelansoft.mywork.dto.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantService(application: Application) {

    private val application = application

    internal suspend fun fetchPlants(plantName: String) {
        withContext(Dispatchers.IO) {
            val service = RetrofitClientInstance.retrofitInstance?.create(IPlantDAO::class.java)
            val plants = async {service?.getAllPlants()}

            updateLocalPlants(plants.await())

        }
    }

    /**
     * Store these plants locally, so that we can use the data without network latency
     */
    private suspend fun updateLocalPlants(plants: ArrayList<Plant>?) {
        var sizeOfPlants = plants?.size
        try {
            var localPlantDAO = getLocalPlantDAO()
            localPlantDAO.insertAll(plants!!)
        }catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message!!)
        }

    }

    internal fun getLocalPlantDAO() : ILocalPlantDAO {
        val db = Room.databaseBuilder(application, PlantDatabase::class.java, "mydiary").build()
        val localPlantDAO = db.localPlantDAO()
        return localPlantDAO
    }

    internal fun save(plant: Plant) {
        getLocalPlantDAO().save(plant)
    }
}