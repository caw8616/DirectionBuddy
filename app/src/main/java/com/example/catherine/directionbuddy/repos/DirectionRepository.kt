package com.example.catherine.directionbuddy.repos

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.example.catherine.directionbuddy.DirectionBuddyDatabase
import com.example.catherine.directionbuddy.entities.Direction
import org.jetbrains.anko.doAsync

class DirectionRepository(var application: Application, var userId: String) {

    private var _allDirections: List<Direction> = listOf<Direction>()
    var mAllDirections : MutableLiveData<List<Direction>> = MutableLiveData()

    private var database : DirectionBuddyDatabase = DirectionBuddyDatabase.getInstance(application)

    init {
        doAsync {
            //Execute all the long running tasks here
            _allDirections = database.directionDao().findDirectionsByUserId(userId)
            mAllDirections.postValue(_allDirections)
        }
    } //init

    fun insertDirection(direction: Direction) {
        doAsync {
            database.directionDao().insert(direction)
            //need to update live data, we are just going to get them all
            //probably should do more efficient way
            _allDirections = database.directionDao().findDirectionsByUserId(userId)
            mAllDirections.postValue(_allDirections)
        }
    }

    fun insertAllDirections(directions: List<Direction>) {
        doAsync {
            database.directionDao().insertAll(directions)
            _allDirections = database.directionDao().findDirectionsByUserId(userId)
            mAllDirections.postValue(_allDirections)
        }
    }

    //additional wrappers for update/delete, etc
//*** swipe to delete
    fun deleteDirection(direction: Direction) {
        doAsync {
            database.directionDao().delete(direction)
            //need to update live data, we are just going to get them all
            //probably should do more efficient way
            _allDirections = database.directionDao().findDirectionsByUserId(userId)
            mAllDirections.postValue(_allDirections)
        }
    }
//***
}