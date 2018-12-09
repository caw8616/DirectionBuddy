package com.example.catherine.directionbuddy.entities

import android.arch.persistence.room.*

//@Entity(tableName = "DirectionData",
//        foreignKeys = [ForeignKey(entity = User::class,
//                parentColumns = arrayOf("id"),
//                childColumns = arrayOf("user_id"),
//                onDelete = ForeignKey.CASCADE)])
@Entity(tableName = "DirectionData",
        indices = [Index("id",unique = true)])
data class Direction(@PrimaryKey(autoGenerate = true) var id: Int?,
                    @ColumnInfo(name = "name") var name: String,
                    @ColumnInfo(name = "address") var address: String,
                    @ColumnInfo(name = "city") var city: String,
                    @ColumnInfo(name = "state") var state: String,
                    @ColumnInfo(name = "zip") var zip: String,
                     @ColumnInfo(name="contact") var contact: String?,
                     @ColumnInfo(name="category") var category: String?,
                     @ColumnInfo(name="user_id") var user_id: String


){
    constructor():this(null,"","","","","","","", "")
    fun getAddressString(): String{
        return address+", "+ city+", "+state+" "+zip
    }
}