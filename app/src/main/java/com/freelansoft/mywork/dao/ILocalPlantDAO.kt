package com.freelansoft.mywork.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.freelansoft.mywork.dto.Plant

@Dao
interface ILocalPlantDAO {

    @Query("SELECT * FROM plant")
    fun getAllPlants()  : LiveData<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(plants: ArrayList<Plant>)

    @Delete
    fun delete(plant: Plant)

    @Insert
    fun save(plant: Plant)
}