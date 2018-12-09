package com.example.catherine.directionbuddy.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.catherine.directionbuddy.entities.Direction

@Dao
interface DirectionDao :BaseDao<Direction> {

    @get:Query("SELECT * FROM DirectionData")
    val all: List<Direction>

    @Query("SELECT * FROM DirectionData WHERE id IN (:directionId)")
    fun loadDirectionById(directionId: Array<Int>): List<Direction>

    @Query("SELECT * FROM DirectionData WHERE user_id = :userId")
    fun findDirectionsByUserId(userId: String): List<Direction>

    @Insert
    fun insertAll(bills: List<Direction>)

// in BaseDAO
//    @Insert
//    fun insert(bill: Invoice)
//
//    @Delete
//    fun delete(bill: Invoice)

}