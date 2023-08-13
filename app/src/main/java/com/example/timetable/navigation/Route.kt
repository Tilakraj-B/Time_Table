package com.example.timetable.navigation

sealed class Route(val route : String){
    object SelectSessionScreen : Route("select_session_screen")
    object SelectSubjectScreen : Route("select_subject_screen")
    object TimeTableScreen : Route("time_table_screen")
    object TempScreeen : Route("temp_screen")
}
