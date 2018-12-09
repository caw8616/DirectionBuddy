package com.example.catherine.directionbuddy.entities

import android.arch.persistence.room.*

@Entity(tableName = "UserData",
        indices = [Index("id",unique = true)])
data class User(@PrimaryKey(autoGenerate = false) var id: Long,
                    @ColumnInfo(name = "username") var username: String,
                    @ColumnInfo(name = "name") var name: String

){
    constructor():this(0,"","")
}