package com.example.timetable.ViewModels

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.timetable.model.Subject
import com.example.timetable.navigation.Route
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class TimeTableViewModel constructor(
    private val context : Context,
    private val navHostController: NavHostController,
    private val sharedViewModel: SharedViewModel
    ) : ViewModel() {


    private val TAG = "TimeTableViewModel"
    private val _state = mutableStateOf(UIState(sharedViewModel.subjectList))
    val state : State<UIState> = _state

    init {
        initalize()
    }

    sealed class UIEvent{
        data class SaveImage(val bitmap : Bitmap, val context : Context) : UIEvent()
    }

    data class UIState(
        var subjectList : List<Pair<Subject, Color>>,
        val subjectMap : MutableMap<Pair<String,String>,MutableList<Pair<String,Color>>> = mutableMapOf(),
        val dayList : List<String> = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
        val timeList : List<String> = listOf(
            "08:00-08:50","09:00-09:50","10:00-10:50",
            "11:00-11:50","12:00-12:50","02:00-02:50",
            "03:00-03:50","04:00-04:50","05:00-05:50",
            "06:00-06:50","07:00-07:50"
        ),
        val timeMap : MutableMap<String,Boolean> = mutableMapOf(),

        )

    fun onEvent(event : UIEvent){
        when(event){
            is UIEvent.SaveImage -> {
                Log.d(TAG, "converting the Bitmap to URI")
                viewModelScope.launch {
                    saveImg(event.bitmap)
                }
            }
            }

    }

    fun saveImg(bitmap : Bitmap){
        try{
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            Log.d(TAG, "saveImg: $root")
            val myDir = File("$root/Time_Table")
            val res = myDir.mkdirs()
            if(res){
                Log.d(TAG, "saveImg: Dir Created")
            }
            else{
                Log.d(TAG, "saveImg: Dir Not Created")
            }
            val file = File(myDir, "${SimpleDateFormat("DDMMYYhhmmss").format(Date())}.jpg")
            Log.d(TAG, "saveImg: file ->  ${file}")
            val ostream = FileOutputStream(file)
            Log.d(TAG, "saveImg: ostream -> $ostream")
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream)
            ostream.flush()
            ostream.close()
            Toast.makeText(context,"Image Saved : ${myDir}",Toast.LENGTH_LONG).show()
        }catch (e : java.lang.Exception){
            Log.d(TAG, "saveImg: ${e.toString()}")
            Toast.makeText(context,"Failed to Save Image",Toast.LENGTH_LONG).show()
        }
    }


    fun initalize(){
        for(subject in state.value.subjectList){
            for(timetable in subject.first.time_table){
                var list = mutableListOf<Pair<String,Color>>()
                if(!_state.value.subjectMap[Pair(timetable[0],timetable[1])].isNullOrEmpty()){
                    list = _state.value.subjectMap[Pair(timetable[0],timetable[1])]!!
                }
                list.add(Pair(subject.first.course_name,subject.second))
                _state.value.subjectMap[Pair(timetable[0],timetable[1])] = list
                _state.value.timeMap[timetable[1]] = true
            }
        }
    }



}


