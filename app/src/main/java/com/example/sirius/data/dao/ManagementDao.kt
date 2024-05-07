package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.sirius.model.Management
import kotlinx.coroutines.flow.Flow

@Dao
interface ManagementDao {

    @Query("SELECT * FROM Management WHERE shelter_id = :id ORDER BY date DESC LIMIT 1")
    fun getLastMovements(id: Int) : Flow<List<Management?>>

    @Query("SELECT * FROM Management WHERE shelter_id = :id")
    fun getMovements(id: Int) : Flow<List<Management?>>


    @Query("SELECT * FROM Management ")
    fun getManagement() : List<Management>
}