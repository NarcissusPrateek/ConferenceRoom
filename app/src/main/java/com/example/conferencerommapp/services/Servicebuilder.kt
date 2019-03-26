package com.example.globofly.services

import com.example.conferencerommapp.services.ConferenceService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Servicebuilder  {
    private const val URL = "http://192.168.1.188/CRB/"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(logger).connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)


    private val builder: Retrofit.Builder = Retrofit.Builder()
                                            .baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create()).client(okHttp.build())
    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>) : T {
        return retrofit.create(serviceType)
    }

    fun getObject() : ConferenceService {
        return buildService(ConferenceService::class.java)
    }
}