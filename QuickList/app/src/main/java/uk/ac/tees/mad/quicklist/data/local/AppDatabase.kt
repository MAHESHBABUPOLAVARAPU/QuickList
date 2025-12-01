package uk.ac.tees.mad.quicklist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 5)
abstract class AppDatabase : RoomDatabase() {


    abstract fun taskDao(): TaskDao


}
