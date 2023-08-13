
import android.graphics.Bitmap
import android.icu.text.ListFormatter.Width
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import coil.compose.rememberAsyncImagePainter
import com.example.timetable.ViewModels.TimeTableViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TimeTable(
    viewModel : TimeTableViewModel
) {
    // Each cell of a column must have the same weight.
    var daySize by remember { mutableStateOf(0.dp) }
    var timeSize by remember { mutableStateOf(0.dp) }
    var subjectHeight by remember { mutableStateOf(0.dp) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    val state = viewModel.state.value
    val scrollState = rememberScrollState()


    Column(
        modifier = Modifier
    ) {
        val snapShot = CaptureBitmap {
            LazyColumn(
                modifier = Modifier
            ) {

                item {
                    Row(
                        modifier = Modifier
                    ) {
                        Text(
                            text = "00:00-00:00",
                            color = Color.White,
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                                .padding(8.dp)
                                .onGloballyPositioned {
                                    daySize = with(density) {
                                        it.size.width.toDp()
                                    }
                                }
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(scrollState, true)
                        ) {
                            for (time in state.timeList) {
                                if (state.timeMap[time] == true) {
                                    Text(
                                        text = time,
                                        modifier = Modifier
                                            .border(1.dp, Color.Black)
                                            .padding(8.dp)
                                            .width(daySize)

                                    )
                                }
                            }
                        }
                    }
                }

                items(state.dayList.size) { index ->
                    val day = state.dayList[index]
                    Row(
                        modifier = Modifier
                            .height(intrinsicSize = IntrinsicSize.Max)
                            .border(1.dp, Color.Black),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                                .fillMaxHeight()
                        ){
                            Text(
                                text = day,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(daySize)
                            )
                        }
                        Row(
                            modifier = Modifier.horizontalScroll(scrollState, true, )
                        ) {
                            for (time in state.timeList) {
                                var subjectlist = mutableListOf<String>()
                                if (state.subjectMap.contains(Pair(day, time))) {
                                    subjectlist = state.subjectMap[Pair(day, time)]!!
                                }
                                if (subjectlist.size == 0) {
                                    subjectlist.add("")
                                }

                                Box(
                                    modifier = Modifier
                                        .border(1.dp, Color.Black)
                                        .fillMaxHeight()
                                        .wrapContentWidth()
                                ) {
                                    Column() {
                                        for (subject in subjectlist) {
                                            Text(
                                                text = subject,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .width(daySize)
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
                item{

                }

            }
        }
        Row(
            modifier = Modifier.padding(10.dp),
        ){
            Button(
                onClick = {
                    coroutineScope.launch {
                        Log.d("NNNNNNNNNNN","NNNNNNNNNN")
                        val bitmap = snapShot.invoke()
                        Log.d("NNNNNNNNNNN"," NNNN22222222 ")
                        viewModel.onEvent(TimeTableViewModel.UIEvent.GetUri(bitmap,context))
                    }
                }
            ) {
                Text(
                    text = "Download Image",
                    modifier = Modifier.padding(5.dp)
                )
            }
            Button(
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Download Pdf",
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

    }
}

@Composable
fun CaptureBitmap(
    content: @Composable () -> Unit,
): () -> Bitmap {

    val context = LocalContext.current

    /**
     * ComposeView that would take composable as its content
     * Kept in remember so recomposition doesn't re-initialize it
     **/
    val composeView = remember { ComposeView(context) }

    /**
     * Callback function which could get latest image bitmap
     **/
    fun captureBitmap(): Bitmap {
        return composeView.drawToBitmap()
    }

    /** Use Native View inside Composable **/
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content.invoke()
                }
            }
        }
    )

    /** returning callback to bitmap **/
    Log.d("TimeTable", "CaptureBitmap: ")
    return ::captureBitmap
}



@Composable
fun ScalableComposable(content: @Composable () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Layout(
        content = content,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset = (offset * zoom + pan)
                }
            }
    ) { measurables, constraints ->
        val placeable = measurables.firstOrNull()?.measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable?.let {
                val offsetX = (constraints.maxWidth - it.width * scale) / 2 + offset.x * scale
                val offsetY = (constraints.maxHeight - it.height * scale) / 2 + offset.y * scale
                val transformedOffset = IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                it.place(transformedOffset)
            }
        }
    }
}




