package com.example.conferencerommapp.Modules

import android.app.Application
import android.content.Context
import com.example.conferencerommapp.Repository.BuildingsRepository
import com.example.conferencerommapp.services.ConferenceService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {
    lateinit var application: Application
    constructor(application: Application) {
        this.application = application
    }


    @Singleton
    @Provides
    fun getApiInterface(retrofit: Retrofit): ConferenceService {
        return retrofit.create(ConferenceService::class.java)
    }

    @Singleton
    @Provides
    fun getRetofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.188/CRB/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun getOkHttpClient(htttLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(htttLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    }

    @Provides
    @Singleton
    fun getContext(): Context {
        return application

    }

    @Provides
    @Singleton
    fun getBuildingsRepo(): BuildingsRepository {
        return BuildingsRepository()
    }
}


