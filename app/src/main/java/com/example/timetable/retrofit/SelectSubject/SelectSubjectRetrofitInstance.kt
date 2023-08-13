package com.example.timetable.retrofit.SelectSubject

import com.example.timetable.retrofit.SelectSession.SelectSessionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SelectSubjectRetrofitInstance {

    private val baseUrl = "http://10.0.2.2:3000/"

    val selectSubjectApi : SelectSubjectApi by lazy{
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().also { client->
                client.connectTimeout(30, TimeUnit.SECONDS)
                client.readTimeout(30, TimeUnit.SECONDS)
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                client.addInterceptor(interceptor)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SelectSubjectApi::class.java)
    }
}