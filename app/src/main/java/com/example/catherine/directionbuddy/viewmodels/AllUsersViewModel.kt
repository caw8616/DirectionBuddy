package com.example.catherine.directionbuddy.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.catherine.directionbuddy.repos.UserRepository
import com.example.catherine.directionbuddy.entities.User

class AllUsersViewModel : AndroidViewModel {
    private var mUserRepository : UserRepository = UserRepository(getApplication())

    constructor(application: Application) : super(application)


    fun getAllUsers(): LiveData<List<User>> {
        return mUserRepository.mAllUsers

    }

    fun insertAllUsers(users : List<User>) {
        mUserRepository.insertAllUsers(users)
    }

    fun insertUser(user: User) {
        mUserRepository.insertUser(user)
    }

    //other wrapper methods for inserting/deleting, etc.
}