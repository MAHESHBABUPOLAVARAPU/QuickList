package uk.ac.tees.mad.quicklist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val notes: String,
    val priority: String,
    val dueDate: Long,  // Changed from String to Long
    val imageUri: String?,
    val completed: Boolean,
    val timestamp: Long,
    val userId: String
)