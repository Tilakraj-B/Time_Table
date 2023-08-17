package com.example.timetable.ViewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.timetable.R
import com.example.timetable.model.Subject

class SharedViewModel constructor(
) : ViewModel(){

    var subjectList = listOf<Subject>()
        private set


    fun getSubjectList(subjectList : List<Subject>){
        this.subjectList = subjectList
    }

    lateinit var Uri : Uri
    lateinit var bitmap : Bitmap

}