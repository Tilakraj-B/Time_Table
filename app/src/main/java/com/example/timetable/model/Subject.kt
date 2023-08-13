package com.example.timetable.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Subject(
    val Department: String,
    val Programm: String,
    val course_ID: String,
    val course_name: String,
    val time_table: List<List<String>> = listOf(listOf())
) : Parcelable