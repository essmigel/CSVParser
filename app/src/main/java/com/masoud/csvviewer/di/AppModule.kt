package com.masoud.csvviewer.di

import com.masoud.csvparser.CsvParser
import com.masoud.csvviewer.data.repository.CsvRepositoryImpl
import com.masoud.csvviewer.domain.repository.CsvRepository
import com.masoud.csvviewer.domain.usecase.GetCsvTableUseCase
import com.masoud.csvviewer.domain.usecase.GetCsvTableUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindCsvRepository(impl: CsvRepositoryImpl): CsvRepository

    @Binds
    abstract fun bindGetCsvTableUseCase(impl: GetCsvTableUseCaseImpl): GetCsvTableUseCase

    companion object {

        private const val CSV_URL =
            "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv"

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

        @Provides
        fun provideCsvParser(): CsvParser = CsvParser()

        @Provides
        @CsvUrl
        fun provideCsvUrl(): String = CSV_URL

        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @DefaultDispatcher
        fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    }
}
