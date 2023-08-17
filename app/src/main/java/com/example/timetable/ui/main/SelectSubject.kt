package com.example.timetable.ui.main

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.DefaultTintColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.timetable.R
import com.example.timetable.ViewModels.SelectSessionViewModel
import com.example.timetable.ViewModels.SelectSubjectViewModel
import com.example.timetable.model.Subject
import com.example.timetable.ui.theme.Purple80
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSubject(
    viewModel: SelectSubjectViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.value
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (state.isFocusedOnTextField)
        Icons.Filled.ArrowBack
    else
        Icons.Filled.List

    var searchText by rememberSaveable { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var focuseManager = LocalFocusManager.current

    viewModel.onEvent(SelectSubjectViewModel.UIEvent.FetchSubjectList)

    val dialogState = rememberMaterialDialogState()
    if(viewModel.state.value.colorPickerDialogState.value == true){
        dialogState.show()
    }

    if(state.errorScreen.value == true){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column {
                Text(
                    text = "Network Error",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 40.sp
                )

                Button(
                    onClick = {
                        viewModel.onEvent(SelectSubjectViewModel.UIEvent.RetryScreen)
                    },
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(text = "Retry")
                }
            }
        }
    }
    else {


        Box(
            modifier = Modifier
        ) {


            MaterialDialog(
                dialogState = dialogState,
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(20.dp)
            ) {
                var currentColor by remember { mutableStateOf(Color.White) }

                Column(
                    modifier = Modifier
                ) {
                    title("Select a Color")
                    colorChooser(
                        colors = ColorPalette.Primary,
                        waitForPositiveButton = false,
                        subColors = ColorPalette.PrimarySub
                    ) { color ->
                        currentColor = color
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {

                        Button(
                            onClick = {
                                viewModel.onEvent(
                                    SelectSubjectViewModel.UIEvent.ChangeColor(
                                        currentColor
                                    )
                                )
                                dialogState.hide()
                            },
                            modifier = Modifier.padding(5.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text(text = "Select")
                        }
                    }
                }
            }







            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Card(
                    elevation = CardDefaults.cardElevation(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                ) {

                    Card(
                        elevation = CardDefaults.cardElevation(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                viewModel.onEvent(
                                    SelectSubjectViewModel.UIEvent.OnSearchTextChange(
                                        it
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .focusRequester(focusRequester),
                            leadingIcon = {
                                Icon(
                                    icon,
                                    contentDescription = "Course List",
                                    modifier = Modifier.clickable {
                                        viewModel.onEvent(
                                            SelectSubjectViewModel.UIEvent.ChangeFocus(
                                                focusRequester,
                                                focuseManager
                                            )
                                        )
                                    }
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }


                    Card(
                        elevation = CardDefaults.cardElevation(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        if (state.isSearchingText || state.isFetchingSubjectList) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (state.isFocusedOnTextField) {
                            LazyColumn(
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                                    .wrapContentHeight()
                            ) {
                                items(state.subjectList.value!!) { item: Subject ->
                                    Text(
                                        text = "(${item.course_ID}) ${item.course_name}",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.onEvent(
                                                    SelectSubjectViewModel.UIEvent.SelectedSubject(
                                                        item
                                                    )
                                                )
                                            }
                                    )
                                    Divider(
                                        thickness = 1.dp,
                                        color = Color.Gray
                                    )
                                }

                            }
                        }
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .fillMaxSize(),
                        shape = RoundedCornerShape(10.dp),

                        ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            if (state.selectedSubjectList.value?.size == 0) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Search Subjects to add to List",
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(state.selectedSubjectList.value!!) { item ->

                                        Card(
                                            modifier = Modifier.padding(5.dp),
                                            elevation = CardDefaults.cardElevation(5.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                IconButton(
                                                    onClick = {
                                                        viewModel.onEvent(
                                                            SelectSubjectViewModel.UIEvent.ChangeStateColorPicker(
                                                                item.first
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.baseline_square_24),
                                                        contentDescription = "Color",
                                                        tint = item.second,
                                                        modifier = Modifier.border(
                                                            1.dp,
                                                            Color.Black
                                                        )
                                                    )
                                                }
                                                Text(
                                                    text = "(${item.first.course_ID}) ${item.first.course_name}",
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        viewModel
                                                            .onEvent(
                                                                SelectSubjectViewModel
                                                                    .UIEvent
                                                                    .RemoveFromSelectedList(
                                                                        item.first,
                                                                        item.second
                                                                    )
                                                            )
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Close,
                                                        contentDescription = "Remove",
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.onEvent(SelectSubjectViewModel.UIEvent.NavigateTimeTable)
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)

                                ) {
                                    Text(text = "Next")
                                }

                            }
                        }
                    }
                }

            }
        }
    }
}









