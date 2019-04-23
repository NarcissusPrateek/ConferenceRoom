package com.example.globofly.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.services.ConferenceService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object ServiceBuilder {

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private lateinit var mContext: Context
    private val okHttp: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor {
            it.proceed(
                it.request().newBuilder()
                    .addHeader("Token", getTokenFromSharedPreference())
                    .addHeader("UserId", getUserIdFromSharedPreference()).build()
            )
        }
    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.IP_ADDRESS)
        .addConverterFactory(GsonConverterFactory.create()).client(okHttp.build())
    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    fun getObject(): ConferenceService {
        return buildService(ConferenceService::class.java)
    }

    fun getObjectWithContext(mContext: Context): ConferenceService {
        this.mContext = mContext
        return buildService(ConferenceService::class.java)
    }

    private fun getTokenFromSharedPreference(): String {
        var pref = mContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        return pref.getString("Token", "Not Set")
    }

    private fun getUserIdFromSharedPreference(): String {
        var pref = mContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        return pref.getString("UserId", "Not Set")
    }
}
