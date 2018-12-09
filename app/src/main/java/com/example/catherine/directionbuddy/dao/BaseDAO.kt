package com.example.catherine.directionbuddy.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Update

@Dao
interface BaseDao<in T> {

    @Insert(onConflict = REPLACE)
    fun insert(t: T)

    @Update
    fun update(t: T)

    @Delete
    fun delete(t: T)
}