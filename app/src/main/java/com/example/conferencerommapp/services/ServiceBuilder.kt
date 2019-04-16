package com.example.globofly.services

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.services.ConferenceService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder  {


    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor {
            it.proceed(it.request().newBuilder().addHeader("Authorization", "Key").build())
        }
    private val builder: Retrofit.Builder = Retrofit.Builder()
                                            .baseUrl(Constants.IP_ADDRESS)
                                            .addConverterFactory(GsonConverterFactory.create()).client(okHttp.build())
    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>) : T {
        return retrofit.create(serviceType)
    }

    fun getObject() : ConferenceService {
        return buildService(ConferenceService::class.java)
    }
}