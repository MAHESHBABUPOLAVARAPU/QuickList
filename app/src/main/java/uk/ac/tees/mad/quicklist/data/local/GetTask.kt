package uk.ac.tees.mad.quicklist.data.local

data class GetTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val notes: String = "",
    val priority: String = "Normal",
    val dueDate: Long = 0L,
    val imageUri: String? = null,
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)