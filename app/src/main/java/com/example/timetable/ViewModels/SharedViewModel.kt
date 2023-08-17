package com.example.timetable.ViewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.timetable.R
import com.example.timetable.model.Subject

class SharedViewModel constructor(
) : ViewModel(){

    var subjectList = listOf<Pair<Subject, Color>>()
        private set


    fun getSubjectList(subjectList : List<Pair<Subject,Color>>){
        this.subjectList = subjectList
    }


}