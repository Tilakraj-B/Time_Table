package com.example.timetable.ViewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.timetable.model.Subject

class SharedViewModel constructor(
) : ViewModel(){

    var subjectList = listOf<Subject>()
        private set

    lateinit var uri : Uri

    fun getSubjectList(subjectList : List<Subject>){
        this.subjectList = subjectList
    }

}