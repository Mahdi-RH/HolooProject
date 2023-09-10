package com.roohandeh.holoomapproject.di

import android.content.Context
import androidx.room.Room
import com.roohandeh.holoomapproject.data.database.LocationsDao
import com.roohandeh.holoomapproject.data.database.LocationsDataBase
import com.roohandeh.holoomapproject.data.network.MapApiService
import com.roohandeh.holoomapproject.data.network.NeshanApiService
import com.roohandeh.holoomapproject.data.repository.MapRepositoryImpl
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import com.roohandeh.holoomapproject.utils.API_KEY
import com.roohandeh.holoomapproject.utils.CONVERT_LOCATION_TO_ADDRESS_API_KEY_VALUE
import com.roohandeh.holoomapproject.utils.BASE_URL
import com.roohandeh.holoomapproject.utils.DATABASE_NAME
import com.roohandeh.holoomapproject.utils.IO_DISPATCHER
import com.roohandeh.holoomapproject.utils.MAIN_DISPATCHER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL).build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { interpolator ->
            val request = interpolator.request()
            val newRequest =
                request.newBuilder()
                    .header(API_KEY, CONVERT_LOCATION_TO_ADDRESS_API_KEY_VALUE)
                    .build()
            interpolator.proceed(newRequest)
        }.build()

    @Singleton
    @Provides
    fun provideMapRepository(
        apiService: MapApiService,
        neshanApiService: NeshanApiService,
        locationsDao: LocationsDao
    ): MapRepository =
        MapRepositoryImpl(apiService, neshanApiService, locationsDao)

    @Singleton
    @Provides
    fun provideNeshanApi(): NeshanApiService =
        NeshanApiService()

    @Singleton
    @Provides
    fun provideLocationsDatabase(
        @ApplicationContext context: Context
    ): LocationsDataBase =
        Room.databaseBuilder(context, LocationsDataBase::class.java, DATABASE_NAME)
            .build()


    @Singleton
    @Provides
    fun provideLocationsDao(dataBase: LocationsDataBase): LocationsDao = dataBase.getDao()


    @Singleton
    @Provides
    fun provideMapApi(retrofit: Retrofit): MapApiService =
        retrofit.create(MapApiService::class.java)


    @Named(IO_DISPATCHER)
    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Named(MAIN_DISPATCHER)
    @Singleton
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

}
