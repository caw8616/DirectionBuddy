package com.example.catherine.directionbuddy

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.catherine.directionbuddy.dao.DirectionDao
import com.example.catherine.directionbuddy.dao.UserDao
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.entities.User

@Database(entities = [(User::class), (Direction::class)],
        version = 1, exportSchema = false)
abstract class DirectionBuddyDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun directionDao(): DirectionDao

    companion object {

        /**
         * The only instance
         */
        private var INSTANCE: DirectionBuddyDatabase? = null

        /**
         * Gets the singleton instance of RoomExampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of RoomExampleDatabase.
         */
        @Synchronized
        fun getInstance(context: Context): DirectionBuddyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context.applicationContext,
                                DirectionBuddyDatabase::class.java,
                                "directions.db")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return INSTANCE!!
        }
    }

}