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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.quicklist.data.BoredRepositoryImpl
import uk.ac.tees.mad.quicklist.data.local.AppDatabase
import uk.ac.tees.mad.quicklist.data.local.TaskDao
import uk.ac.tees.mad.quicklist.data.remote.api.BoredApiService
import uk.ac.tees.mad.quicklist.domain.reposiotry.BoredRepository
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


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bored-api.appbrewery.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideBoredApi(retrofit: Retrofit): BoredApiService {
        return retrofit.create(BoredApiService::class.java)
    }


    @Provides
    @Singleton
    fun providesBoardRepository(boredApiService: BoredApiService): BoredRepository {
        return BoredRepositoryImpl(boredApiService)

    }


}