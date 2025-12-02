package uk.ac.tees.mad.quicklist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

import androidx.room.Query





    @Dao
    interface TaskDao {

        @Query("SELECT * FROM task ORDER BY timestamp DESC")
        suspend fun getAllTasks(): List<TaskEntity>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upsert(task: TaskEntity)

        @Query("DELETE FROM task WHERE id = :taskId")
        suspend fun deleteTaskById(taskId: String)
    }