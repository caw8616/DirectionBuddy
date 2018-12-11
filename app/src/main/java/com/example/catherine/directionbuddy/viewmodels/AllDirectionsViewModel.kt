package com.example.catherine.directionbuddy.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.catherine.directionbuddy.repos.DirectionRepository
import com.example.catherine.directionbuddy.entities.Direction

class AllDirectionsViewModel : AndroidViewModel {
    private var mDirectionsRepository : DirectionRepository? = null
    private var userId: String = ""


    constructor(application: Application, useId: String) : super(application) {
        userId = useId
        mDirectionsRepository = DirectionRepository(getApplication(), userId!!)
    }


    fun getAllDirections(): LiveData<List<Direction>> {
        return mDirectionsRepository!!.mAllDirections
    }
    fun getDiectionById(directionId: Int): LiveData<List<Direction>>{
        mDirectionsRepository!!.getByDirectionId(directionId)
        return mDirectionsRepository!!.mAllDirections
    }

    fun insertDirection(direction: Direction) {
        mDirectionsRepository!!.insertDirection(direction)
    }
    fun insertAll(directions: List<Direction>) {
        mDirectionsRepository!!.insertAllDirections(directions)
    }

    fun updateDirection(direction: Direction) {
        mDirectionsRepository!!.updateDirection(direction)
    }

    //other wrapper methods for inserting/deleting, etc.
//swipe to delete
    fun deleteDirection(direction: Direction) {
        mDirectionsRepository!!.deleteDirection(direction)
    }
//***
}