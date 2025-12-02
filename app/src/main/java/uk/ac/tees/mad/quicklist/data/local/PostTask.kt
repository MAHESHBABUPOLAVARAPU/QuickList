package uk.ac.tees.mad.quicklist.data.local

data class PostTask(
    val id: String,
    val title: String,
    val description: String,
    val notes: String,
    val priority: String,
    val dueDate: String,
    val imageUri: String?,
    val completed: Boolean,
    val timestamp: Long,
    val userId: String
)
