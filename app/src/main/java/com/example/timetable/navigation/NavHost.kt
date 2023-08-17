package com.example.timetable.navigation

import SelectSession
import TimeTable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timetable.ViewModels.SelectSessionViewModel
import com.example.timetable.ViewModels.SelectSubjectViewModel
import com.example.timetable.ViewModels.SharedViewModel
import com.example.timetable.ViewModels.TimeTableViewModel
import com.example.timetable.model.Subject
import com.example.timetable.repository.SelectSessionRepo
import com.example.timetable.repository.SelectSubjectRepo
import com.example.timetable.retrofit.SelectSession.SelectSessionRetrofitInstance
import com.example.timetable.retrofit.SelectSubject.SelectSubjectRetrofitInstance
import com.example.timetable.ui.main.SelectSubject

@Composable
fun NavHost(
    navController: NavHostController
){

    val sharedViewModel : SharedViewModel = viewModel()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = Route.SelectSessionScreen.route,
    ){

        composable(
            route = Route.SelectSessionScreen.route
        ){
            SelectSession(
                viewModel = SelectSessionViewModel(
                    navController = navController,
                    repo = SelectSessionRepo(SelectSessionRetrofitInstance.selectSessionApi),
                    context = LocalContext.current
                )
            )
        }

        composable(
            route = Route.SelectSubjectScreen.route + "/{session}" + "/{season}",
            arguments = listOf(
                navArgument(name = "session"){
                    type = NavType.StringType
                },
                navArgument(name = "season"){
                    type = NavType.StringType
                }
            )
        ){arg->
            SelectSubject(
                viewModel = SelectSubjectViewModel(
                    context = LocalContext.current,
                    navController = navController,
                    repo = SelectSubjectRepo(SelectSubjectRetrofitInstance.selectSubjectApi),
                    session = arg.arguments!!.getString("session")!!,
                    season = arg.arguments!!.getString("season")!!,
                    sharedViewModel = sharedViewModel
                )
            )
        }

        composable(
            route = Route.TimeTableScreen.route,
        ){
            TimeTable(
                viewModel = TimeTableViewModel(
                    context = LocalContext.current,
                    navHostController = navController,
                    sharedViewModel = sharedViewModel
                )
            )
        }

    }

}
