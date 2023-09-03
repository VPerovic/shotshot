import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column {
            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }

            DrawingScreen()
        }


        val canvasModifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {

            }

        Canvas(modifier = canvasModifier) {

        }
    }
}

@Composable
private fun LogPointerEvents(filter: PointerEventType? = null) {
    var log by remember { mutableStateOf("log") }
    Column {
        Text(log)
        Box(
            Modifier
                .size(100.dp)
                .background(Color.Red)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            // handle pointer event
                            if (filter == null || event.type == filter) {
                                log = "${event.type}, ${event.changes.first().position}"
                            }
                        }
                    }
                }


        )
    }
}

@Composable
private fun SimpleDraggable(onClick: () -> Unit) {
    var currentLine by remember { mutableStateOf(Line.Unspecified) }
    val lines = remember { mutableStateListOf<Line>() }

    Canvas(Modifier
        .size(100.dp)
        .background(Color(red = 140, green = 150, blue = 160))
        .pointerInput(true) {
            detectDragGestures(
                onDragStart = { offset -> currentLine = Line(offset, offset) },
                onDragEnd = {
                    lines.add(currentLine)
                    currentLine = Line.Unspecified
                },
                onDragCancel = {
                    currentLine = Line.Unspecified
                },
                onDrag = { change, _ ->
                    currentLine = Line(currentLine.start, change.position)
                }
            )
        }
    ) {
        for (line in lines) {
            drawLine(
                start = line.start,
                end = line.end,
                strokeWidth = line.strokeWidth.toPx(),
                color = line.color
            )
        }
        if (currentLine != Line.Unspecified) {
            drawLine(
                start = currentLine.start,
                end = currentLine.end,
                strokeWidth = currentLine.strokeWidth.toPx(),
                color = currentLine.color
            )
        }
    }
}
@Composable
fun DrawingScreen() {
    val lines = remember {
        mutableStateListOf<Line>()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    val line = Line(
                        start = change.position - dragAmount,
                        end = change.position
                    )

                    lines.add(line)
                }
            }
    ) {
        lines.forEach { line ->
            drawLine(
                color = line.color,
                start = line.start,
                end = line.end,
                strokeWidth = line.strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}


data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
) {
    companion object {
        val Unspecified = Line(Offset.Unspecified, Offset.Unspecified, Color.Unspecified, Dp.Unspecified)
    }
}


@Composable
private fun SimpleClickable(onClick: () -> Unit) {
    Box(
        Modifier
            .size(100.dp)
            .background(Color.Magenta)
            .pointerInput(onClick) {
                awaitEachGesture {
                    awaitFirstDown().also { it.consume() }
                    val up = waitForUpOrCancellation()
                    if (up != null) {
                        up.consume()
                        onClick()
                    }
                }
            }
    )
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
