package com.example.timetable.ViewModels

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.toSpannable
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.timetable.MainActivity
import com.example.timetable.model.Subject
import com.example.timetable.navigation.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.ArrayList
import kotlin.coroutines.coroutineContext

class TimeTableViewModel constructor(
    private val context : Context,
    private val navHostController: NavHostController,
    private val sharedViewModel: SharedViewModel
    ) : ViewModel() {


    private val TAG = "TimeTableViewModel"
    private val _state = mutableStateOf(UIState(sharedViewModel.subjectList))
    val state : State<UIState> = _state

    init {
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
                val androidView = event.androidView
        }

            is UIEvent.Loading -> {

            }
            is UIEvent.GetUri -> {
                Log.d(TAG, "converting the Bitmap to URI")
                viewModelScope.launch {
                    // In your activity or fragment

                    // Ensure you have the WRITE_EXTERNAL_STORAGE permission granted before proceeding.

// The content resolver is used to interact with the media store
                    val resolver: ContentResolver = context.contentResolver

// Create a ContentValues object to hold the image metadata
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "my_image.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

// Insert the image into the MediaStore
                    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

// Open an output stream to the image Uri and write the image data
                    imageUri?.let {
                        val outputStream = resolver.openOutputStream(it)
                        event.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream?.close()

                        // Optionally, notify the MediaStore to scan the new image
                        MediaScannerConnection.scanFile(context, arrayOf(imageUri.path), null, null)
                    }

                    Log.d(TAG, "onEvent: Saved Image Successfully")
                }
            }
        }

    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        Log.d(TAG, "getImageUriFromBitmap: ${Uri.parse(path)}")
        Log.d(TAG, "getImageUriFromBitmap: ${state}")
        return Uri.parse(path)
    }

}


