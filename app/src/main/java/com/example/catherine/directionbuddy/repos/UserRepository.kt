package com.example.catherine.directionbuddy.repos

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.example.catherine.directionbuddy.DirectionBuddyDatabase
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.entities.User
import org.jetbrains.anko.doAsync

class UserRepository(var application: Application) {

    private var _allUsers: List<User> = listOf<User>()
    var mAllUsers : MutableLiveData<List<User>> = MutableLiveData()

    private val _allDirections: List<Direction> = listOf<Direction>()
    var mAllDirections : MutableLiveData<List<Direction>> = MutableLiveData()

    private var database : DirectionBuddyDatabase = DirectionBuddyDatabase.getInstance(application)

    init {
        doAsync {
            //Execute all the long running tasks here
            _allUsers = database.userDao().all
            mAllUsers.postValue(_allUsers)
        }
    } //init

//    fun getAllCustomers(): List<Customer> {
//        return _allCustomers
//    }

    fun insertUser(user: User) {
        doAsync {
            database.userDao().insert(user)
            //need to update live data, we are just going to get them all
            //probably should do more efficient way
            _allUsers = database.userDao().all
            mAllUsers.postValue(_allUsers)
        }
    }

    fun insertAllUsers(users: List<User>) {
        doAsync {
            database.userDao().insertAll(users)
            _allUsers = database.userDao().all
            mAllUsers.postValue(_allUsers)
        }
    }

    //additional wrappers for update/delete, etc
}