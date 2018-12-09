package com.example.catherine.directionbuddy.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.catherine.directionbuddy.entities.User

@Dao
interface UserDao : BaseDao<User>{

    @get:Query("SELECT * FROM UserData ORDER BY username ASC")
    val all: List<User>

    @Query("SELECT * FROM UserData WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: Array<Int>): List<User>

    @Query("SELECT * FROM UserData WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): User

    @Query("SELECT * FROM UserData WHERE username LIKE :username LIMIT 1")
    fun findByUsername(username: String): User

    @Insert
    fun insertAll(users: List<User>)

//in BaseDAO
//    @Insert(onConflict = REPLACE)
//    fun insert(customerData: Customer)
//
//    @Delete
//    fun delete(client: Customer)

}