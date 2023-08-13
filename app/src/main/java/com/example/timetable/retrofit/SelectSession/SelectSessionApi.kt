package com.example.timetable.retrofit.SelectSession

import com.example.timetable.model.Subject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SelectSessionApi {

    @GET("/get/session")
    suspend fun getSessionList() : Response<List<String>>

    @GET("/get/season/")
    suspend fun getSeasonList(
        @Query("session") session:String
    ) : Response<List<String>>



}