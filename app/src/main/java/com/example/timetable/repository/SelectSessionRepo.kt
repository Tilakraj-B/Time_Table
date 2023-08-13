package com.example.timetable.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.timetable.model.Subject
import com.example.timetable.retrofit.SelectSession.SelectSessionApi
import com.example.timetable.retrofit.SelectSession.SelectSessionRetrofitInstance

class SelectSessionRepo(
    private val api : SelectSessionApi
    ) {
    private val TAG = "SelectSessionRepo"
    val sessionList = MutableLiveData<List<String>>()
    val seasonList = MutableLiveData<List<String>>()


    suspend fun getSessionList(){
        Log.d(TAG, "getSessionList function invoked")
        val response = api.getSessionList()
        if(response.isSuccessful){
            sessionList.postValue(response.body())
        }
        else{
            Log.d(TAG,"failed to Get SessionList")
        }
    }

    suspend fun getSeasonList(session : String){
        Log.d(TAG, "getSeasonList function invoked")
        val response = api.getSeasonList(session)
        if(response.isSuccessful){
            seasonList.postValue(response.body())
        }
        else{
            Log.d(TAG,"failed to get Season List")
        }
    }





}