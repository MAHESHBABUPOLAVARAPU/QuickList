package uk.ac.tees.mad.quicklist.di



import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.ac.tees.mad.quicklist.data.local.AppDatabase
import uk.ac.tees.mad.quicklist.data.local.TaskDao
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {


    @Provides
    @Singleton
    fun providesDB(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providesDao(db: AppDatabase): TaskDao {
        return db.taskDao()
    }

}