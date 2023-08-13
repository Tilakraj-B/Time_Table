package com.example.timetable.retrofit.SelectSubject

import com.example.timetable.model.Subject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SelectSubjectApi {

    @GET("/get/TT/")
    suspend fun getSubjectList(
        @Query("session") session : String,
        @Query("season") season : String
    ) : Response<List<Subject>>
}