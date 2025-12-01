package uk.ac.tees.mad.quicklist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.intellij.lang.annotations.PrintFormat

@Entity("task_table")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val timestamp: Long,
    val userId: String,
)
