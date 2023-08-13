package com.example.timetable.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.timetable.model.Subject
import com.example.timetable.retrofit.SelectSubject.SelectSubjectApi

class SelectSubjectRepo(
    private val api : SelectSubjectApi
) {

    val subjectList  = MutableLiveData<List<Subject>>()
    private val TAG = "SelectSubjectRepo"

    suspend fun getSubjectList(session : String ,season : String){
        Log.d(TAG,"getSubjectList invoked")
        val response = api.getSubjectList(session, season);
        if(response.isSuccessful){
            subjectList.postValue(response.body())
        }
        else{
            Log.d(TAG,response.errorBody().toString())
        }
    }

}