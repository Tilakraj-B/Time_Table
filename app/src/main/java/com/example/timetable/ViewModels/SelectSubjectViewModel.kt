package com.example.timetable.ViewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.timetable.model.Subject
import com.example.timetable.navigation.Route
import com.example.timetable.repository.SelectSubjectRepo
import kotlinx.coroutines.launch
import org.json.JSONObject.NULL
import kotlin.collections.ArrayList

class SelectSubjectViewModel constructor(
    private val context : Context,
    private val navController : NavHostController,
    private val repo : SelectSubjectRepo,
    private val session : String,
    private val season : String,
    private val sharedViewModel: SharedViewModel

) : ViewModel() {

    private val TAG = "SelectSubjectViewModel"
    private val _state = mutableStateOf(UIState());
    val state : State<UIState> = _state
    var text = ""

     sealed class UIEvent{
         data class SelectedSubject(val currentSubject : Subject) : UIEvent()
         object ExpandSubjectList : UIEvent()
         object FetchSubjectList : UIEvent()
         data class OnSearchTextChange(val searchText : String) : UIEvent()
         data class ChangeFocus(val focusRequester: FocusRequester,val focusManager: FocusManager) : UIEvent()
         data class RemoveFromSelectedList(val subject : Subject) : UIEvent()
         object NavigateTimeTable : UIEvent()
         object ChangeStateColorPicker : UIEvent()
     }

     data class UIState(
         var isSearchingText : Boolean = false,
         var isSubjectExpanded: Boolean = false,
         var isFetchingSubjectList: Boolean = true,
         var selectedSubjectList: MutableLiveData<MutableList<Subject>> = MutableLiveData(mutableListOf()),
         val allSubjectList: MutableLiveData<List<Subject>> = MutableLiveData<List<Subject>>(),
         val subjectList : MutableLiveData<List<Subject>> = allSubjectList,
         var isFocusedOnTextField : Boolean = false,
         var colorPickerDialogState : MutableLiveData<Boolean> = MutableLiveData(false)
     )


    fun onEvent(event : UIEvent){
        when (event){
            is UIEvent.FetchSubjectList -> {
                if(state.value.isFetchingSubjectList){
                    Log.d(TAG,"event -> FetchSubjectList")
                    viewModelScope.launch {
                        repo.getSubjectList(session,season)
                        _state.value = state.value.copy(
                            allSubjectList = repo.subjectList,
                            subjectList = repo.subjectList,
                            isFetchingSubjectList = false
                        )
                    }
                }
            }

            is UIEvent.ExpandSubjectList -> {
                _state.value = state.value.copy(
                    isSubjectExpanded = !state.value.isSubjectExpanded
                )
            }
            is UIEvent.SelectedSubject -> {
                val list = state.value.selectedSubjectList.value
                if(!list!!.contains(event.currentSubject)){
                    list.add(event.currentSubject)
                }

                _state.value = state.value.copy(
                    selectedSubjectList = MutableLiveData(list)
                )
                Log.d(TAG, "${state.value.selectedSubjectList.value}")
            }

            is UIEvent.OnSearchTextChange -> {
                if(event.searchText.isBlank()){
                    _state.value = state.value.copy(
                        subjectList = state.value.allSubjectList
                    )
                    return
                }
                else{
                    viewModelScope.launch {
                        _state.value = state.value.copy(
                            isSearchingText = true
                        )
                        _state.value = state.value.copy(
                            subjectList = MutableLiveData(state.value.allSubjectList.value?.filter {subject: Subject ->
                                subject.course_name.contains(event.searchText,ignoreCase = true) || subject.course_ID.contains(event.searchText,ignoreCase = true)

                            }),
                            isFocusedOnTextField = true
                        )
                        _state.value = state.value.copy(
                            isSearchingText = false
                        )
                    }

                }
            }

            is UIEvent.ChangeFocus -> {
                if(state.value.isFocusedOnTextField){
                    event.focusManager.clearFocus()
                }
                else{
                    event.focusRequester.requestFocus()
                }
                _state.value = state.value.copy(
                    isFocusedOnTextField = !state.value.isFocusedOnTextField
                )
            }

            is UIEvent.RemoveFromSelectedList -> {
                val list = state.value.selectedSubjectList.value
                list!!.remove(event.subject)
                _state.value = state.value.copy(
                    selectedSubjectList = MutableLiveData(list)
                )
            }

            is UIEvent.NavigateTimeTable -> {
                viewModelScope.launch {
                    sharedViewModel.getSubjectList(state.value.selectedSubjectList.value!!.toList())
                    navController.navigate(Route.TimeTableScreen.route)
                }
            }

            is UIEvent.ChangeStateColorPicker -> {
                _state.value = state.value.copy(
                    colorPickerDialogState = MutableLiveData(!state.value.colorPickerDialogState.value!!)
                )
            }
        }
    }
}



