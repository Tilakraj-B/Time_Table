package com.example.timetable.ViewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.timetable.model.Subject
import com.example.timetable.navigation.Route
import com.example.timetable.repository.SelectSessionRepo
import kotlinx.coroutines.launch

class SelectSessionViewModel constructor(
    private val navController: NavHostController,
    private val repo: SelectSessionRepo,
    private val context: Context
): ViewModel() {

    private val TAG = "SelectSessionViewModel"

    private val _state = mutableStateOf(UIState());
    val state : State<UIState> = _state;

    sealed class UIEvent{
        object ExpandSessionList :UIEvent()
        object ExpandSeasonList : UIEvent()
        data class SelectedSession(val currentSession: String) : UIEvent()
        data class SelectedSeason(val currentSeason : String) : UIEvent()
        object FetchSessionList : UIEvent()
        object FetchSeasonList : UIEvent()
        data class NavigateSelectSubject (val session: String, val season : String): UIEvent()
    }

     data class UIState(
         var isFetchingSessionList : Boolean = true,
         var isFetchingSeasonList : Boolean = false,
         var isSessionExpanded : Boolean = false,
         var isSeasonExpanded : Boolean = false,
         var selectedSession : String = "Select Session",
         var selectedSeason : String = "Select Season",
         val sessionList: MutableLiveData<List<String>> = MutableLiveData<List<String>>(),
         val seasonList : MutableLiveData<List<String>> = MutableLiveData<List<String>>(),
         val subjectList: MutableLiveData<List<Subject>> = MutableLiveData<List<Subject>>(),
         )



    fun onEvent(event : UIEvent){
        when(event){

            is UIEvent.FetchSessionList->{
                if(state.value.isFetchingSessionList){
                    viewModelScope.launch{
                        Log.d(TAG,"fetching the session list")
                        repo.getSessionList();
                        _state.value = state.value.copy(
                            sessionList = repo.sessionList
                        )
                        _state.value = state.value.copy(
                            isFetchingSessionList = false
                        )
                    }
                }
            }

            is UIEvent.FetchSeasonList -> {

                    viewModelScope.launch {
                        Log.d(TAG, "fetching the season list")
                        repo.getSeasonList(state.value.selectedSession)
                        _state.value = state.value.copy(
                            seasonList = repo.seasonList
                        )
                        _state.value = state.value.copy(
                            isFetchingSeasonList = false
                        )
                    }


            }

            is UIEvent.ExpandSessionList ->{
                _state.value = state.value.copy(
                    isSessionExpanded = !state.value.isSessionExpanded
                )
                Log.d(TAG,"${state.value.isSessionExpanded}")
            }

            is UIEvent.ExpandSeasonList ->{
                if(state.value.selectedSession == "Select Session"){
                    Toast.makeText(context,"Select Session First",Toast.LENGTH_LONG).show()
                }
                else{
                    _state.value = state.value.copy(
                        isSeasonExpanded = !state.value.isSeasonExpanded
                    )
                    Log.d(TAG,"${state.value.isSeasonExpanded}")
                }
            }


            is UIEvent.SelectedSession -> {
                _state.value = state.value.copy(
                    selectedSession = event.currentSession
                )
            }

            is UIEvent.SelectedSeason -> {
                _state.value = state.value.copy(
                    selectedSeason = event.currentSeason
                )
            }

            is UIEvent.NavigateSelectSubject -> {
                Log.d(TAG,"in navigate select subject")
                if(state.value.selectedSeason == "Select Season"){
                    Toast.makeText(context,"Select Season",Toast.LENGTH_LONG).show()
                }
                else{
                    navController.navigate(Route.SelectSubjectScreen.route + "/${event.session}" + "/${event.season}")
                }
            }

        }
    }
    

}

