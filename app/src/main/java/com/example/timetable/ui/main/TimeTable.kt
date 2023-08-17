
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import coil.compose.rememberAsyncImagePainter
import com.example.timetable.ViewModels.TimeTableViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


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




@Composable
fun TimeTable(
    viewModel: TimeTableViewModel,
){
    val state = viewModel.state.value
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val blockWidth = screenWidth/(state.timeMap.size + 1)
    var Style = MaterialTheme.typography.bodySmall
    var textStyle by remember{ mutableStateOf(Style) }
    val context = LocalContext.current
    textStyle = textStyle.copy(
        textAlign = TextAlign.Center
    )
    val screenHeight = configuration.screenHeightDp.dp


    Box(
        modifier = Modifier
    ) {



        Column(
            modifier = Modifier
                .onGloballyPositioned {
//                val columnSize = it.size.height.dp
//                if(columnSize > screenHeight){
//                    textStyle = textStyle.copy(
//                        fontSize = textStyle.fontSize * 0.95
//                    )
//                }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val bitmapImage = CaptureBitmap {
                Column(
                    modifier = Modifier
                        .padding(1.dp)
                        .background(Color.White),

                    ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(blockWidth)
                                .border(1.dp, Color.Black)
                        ) {
                            Text(
                                text = "00:00-00:00",
                                modifier = Modifier.padding(5.dp),
                                style = textStyle.copy(
                                    color = Color.White
                                ),
                                softWrap = false,
                                onTextLayout = { result ->
                                    if (result.didOverflowWidth) {
                                        textStyle = textStyle.copy(
                                            fontSize = textStyle.fontSize * 0.95
                                        )
                                    }
                                }
                            )
                        }
                        for (time in state.timeList) {
                            if (state.timeMap[time] == true) {

                                Box(
                                    modifier = Modifier
                                        .width(blockWidth)
                                        .border(1.dp, Color.Black)
                                ) {
                                    Text(
                                        text = time,
                                        modifier = Modifier.padding(5.dp),
                                        style = textStyle,
                                        softWrap = false,
                                    )
                                }
                            }
                        }
                    }

                    for (day in state.dayList) {
                        Row(
                            modifier = Modifier
                                .height(intrinsicSize = IntrinsicSize.Max)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(blockWidth)
                                    .border(1.dp, Color.Black)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = day,
                                    modifier = Modifier.padding(5.dp),
                                    style = textStyle,
                                    softWrap = false,
                                )
                            }
                            for (time in state.timeList) {
                                if (state.timeMap[time] == true) {

                                    var subjectList = mutableListOf<String>()
                                    if (state.subjectMap.contains(Pair(day, time))) {
                                        subjectList = state.subjectMap[Pair(day, time)]!!
                                    }
                                    if (subjectList.size == 0) {
                                        subjectList.add(" ")
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .border(1.dp, Color.Black)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .wrapContentHeight()

                                        ) {
                                            for (subject in subjectList) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(blockWidth)
                                                        .padding(5.dp)
                                                ) {
                                                    Text(
                                                        text = subject,
                                                        style = textStyle,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }

                }
            }

            Button(
                onClick = {
                    val image = bitmapImage.invoke()
                    viewModel.onEvent(TimeTableViewModel.UIEvent.GetUri(image, context))
                }

            ) {
                Text(text = "Save Image")
            }
        }
    }



}





