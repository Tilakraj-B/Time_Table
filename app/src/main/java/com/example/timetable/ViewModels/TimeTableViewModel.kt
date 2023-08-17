package com.example.timetable.ViewModels

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
        object Loading : UIEvent()
        data class GetImage(val androidView : Unit) : UIEvent()
        data class GetUri(val bitmap : Bitmap, val context : Context) : UIEvent()

    }

    data class UIState(
        var subjectList : List<Subject>,
        val subjectMap : MutableMap<Pair<String,String>,MutableList<String>> = mutableMapOf(),
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
            is UIEvent.GetImage -> {
              }

            is UIEvent.Loading -> {

            }
            is UIEvent.GetUri -> {
                Log.d(TAG, "converting the Bitmap to URI")
                viewModelScope.launch {
                    // In your activity or fragment
                    // Ensure you have the WRITE_EXTERNAL_STORAGE permission granted before proceeding.
                    sharedViewModel.bitmap = event.bitmap
                    saveImg(event.bitmap)
                    navHostController.navigate(Route.NewScreen.route)
                }
            }
            }

    }

    fun saveImg(bitmap : Bitmap){
        try{
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            Log.d(TAG, "saveImg: $root")
            val myDir = File("$root/new")
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
        }catch (e : java.lang.Exception){
            Log.d(TAG, "saveImg: ${e.toString()}")
        }
    }

    fun initalize(){
        for(subject in state.value.subjectList){
            for(timetable in subject.time_table){
                var list = mutableListOf<String>()
                if(!_state.value.subjectMap[Pair(timetable[0],timetable[1])].isNullOrEmpty()){
                    list = _state.value.subjectMap[Pair(timetable[0],timetable[1])]!!
                }
                list.add(subject.course_name)
                _state.value.subjectMap[Pair(timetable[0],timetable[1])] = list
                _state.value.timeMap[timetable[1]] = true
            }
        }
        Log.d(TAG, "SSSSS: ${state.value.timeMap.size}")
        Log.d(TAG, "SSSSS: ${state.value.timeMap} ")
    }



}


