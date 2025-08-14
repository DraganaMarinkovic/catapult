package com.example.catapult.di

import android.content.Context
import androidx.room.Room
import com.example.catapult.db.AccountDao
import com.example.catapult.db.BreedDao
import com.example.catapult.db.CatapultDatabase
import com.example.catapult.db.LeaderboardDao
import com.example.catapult.db.QuizDao
import com.example.catapult.network.CatApi
import com.example.catapult.repository.BreedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): CatapultDatabase =
        Room.databaseBuilder(ctx, CatapultDatabase::class.java, "catapult.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideBreedDao(db: CatapultDatabase): BreedDao = db.breedDao()

    @Provides fun provideAccountDao(db: CatapultDatabase): AccountDao = db.accountDao()

    @Provides fun provideQuizDao(db: CatapultDatabase): QuizDao = db.quizDao()

    @Provides fun provideLeaderboardDao(db: CatapultDatabase): LeaderboardDao = db.leaderboardDao()

    @Provides @Singleton
    fun provideBreedRepository(
        api: CatApi,
        dao: BreedDao
    ): BreedRepository = BreedRepository(api, dao)
}
