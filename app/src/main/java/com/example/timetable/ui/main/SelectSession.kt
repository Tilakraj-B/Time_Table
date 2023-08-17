



import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.timetable.ViewModels.SelectSessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Creating a composable
// function to display Top Bar


// Creating a composable function
// to create an Outlined Text Field
// Calling this function as content
// in the above function
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSession(
    modifier : Modifier = Modifier,
    viewModel: SelectSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){

    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.value
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (state.isSessionExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    viewModel.onEvent(SelectSessionViewModel.UIEvent.FetchSessionList)

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
                        viewModel.onEvent(SelectSessionViewModel.UIEvent.RetryConnection)
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier,

                ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Create an Outlined Text Field
                    // with icon and not expanded
                    OutlinedTextField(
                        value = state.selectedSession,
                        onValueChange = { state.selectedSession = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This value is used to assign to
                                // the DropDown the same width
                                mTextFieldSize = coordinates.size.toSize()
                            },
                        label = { Text("Session") },
                        trailingIcon = {
                            Icon(icon, "contentDescription",
                                Modifier.clickable {
                                    viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSessionList)
                                })
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Color.Transparent,
                        )
                    )
                    // Create a drop-down menu with list of cities,
                    // when clicked, set the Text Field text as the city selected
                    DropdownMenu(
                        expanded = state.isSessionExpanded,
                        onDismissRequest = {
                            viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSessionList)
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                    ) {

                        state.sessionList.value?.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label) },
                                onClick = {
                                    state.selectedSession = label

                                    coroutineScope.launch {
                                        viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSessionList)
                                        state.isFetchingSeasonList = !state.isFetchingSeasonList
                                        viewModel.onEvent(SelectSessionViewModel.UIEvent.FetchSeasonList)
                                    }

                                })
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Create an Outlined Text Field
                    // with icon and not expanded
                    OutlinedTextField(
                        value = state.selectedSeason,
                        onValueChange = { state.selectedSeason = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This value is used to assign to
                                // the DropDown the same width
                                mTextFieldSize = coordinates.size.toSize()
                            },
                        label = { Text("Season") },
                        trailingIcon = {
                            Icon(icon, "contentDescription",
                                Modifier.clickable {
                                    viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSeasonList)
                                })
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Color.Transparent,
                        )
                    )
                    // Create a drop-down menu with list of cities,
                    // when clicked, set the Text Field text as the city selected
                    DropdownMenu(
                        expanded = state.isSeasonExpanded,
                        onDismissRequest = {
                            viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSeasonList)
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                    ) {

                        state.seasonList.value?.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label) },
                                onClick = {
                                    state.selectedSeason = label
                                    viewModel.onEvent(SelectSessionViewModel.UIEvent.ExpandSeasonList)
                                })
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.onEvent(
                                SelectSessionViewModel.UIEvent.NavigateSelectSubject(
                                    state.selectedSession,
                                    state.selectedSeason
                                )
                            )
                        }
                    ) {
                        Text(text = "Next")
                    }

                }
            }

        }
    }
}



