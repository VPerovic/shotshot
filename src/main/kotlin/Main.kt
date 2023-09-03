import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

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
//            SimpleDraggable {  }
//            DrawingFreehandScreen()
//            DrawingRectScreen()
            DrawingStepsScreen()
//            DrawingScreen()
//            DrawingArrowScreen()
        }
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
                    change.consume()
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
fun DrawingFreehandScreen() {
    val lines = remember {
        mutableStateListOf<Line>()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
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
            drawCircle(
                color = Color.Red,
                radius = 4.0f,
                center = line.start
            )
        }
    }
}

@Composable
fun DrawingLineScreen() {
    val lines = remember {
        mutableStateListOf<Line>()
    }

    var currentLine by remember { mutableStateOf(Line.Unspecified) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
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
                    onDrag = { change, dragAmount ->
                        change.consume()

//                        val line = Line(
//                            start = change.position - dragAmount,
//                            end = change.position
//                        )

//                        lines.add(line)

                        currentLine = Line(currentLine.start, change.position)
                    })
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
        if (currentLine != Line.Unspecified) {
            drawLine(
                color = currentLine.color,
                start = currentLine.start,
                end = currentLine.end,
                strokeWidth = currentLine.strokeWidth.toPx(),
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
fun DrawingArrowScreen() {
    val arrows = remember {
        mutableStateListOf<Line>()
    }

    var currentArrow by remember { mutableStateOf(Line.Unspecified) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset -> currentArrow = Line(offset, offset) },
                    onDragEnd = {
                        arrows.add(currentArrow)
                        currentArrow = Line.Unspecified
                    },
                    onDragCancel = {
                        currentArrow = Line.Unspecified
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentArrow = Line(currentArrow.start, change.position)
                    })
            }
    ) {
        arrows.forEach { arrow ->
            drawArrow(arrow)
        }
        if (currentArrow != Line.Unspecified) {
            drawArrow(currentArrow)
        }
    }
}

private fun DrawScope.drawArrow(currentArrow: Line) {
    val arrowLength = sqrt(
        (currentArrow.start.x - currentArrow.end.x).toDouble().pow(2) +
                (currentArrow.start.y - currentArrow.end.y).pow(2)
    ).toFloat()

    val arrowAngle =
        atan2(currentArrow.end.y - currentArrow.start.y, currentArrow.end.x - currentArrow.start.x) * 180 / Math.PI;
    withTransform({
        translate(currentArrow.start.x, currentArrow.start.y)
        rotate(arrowAngle.toFloat(), Offset.Zero)
    }) {

        drawLine(
            color = currentArrow.color,
            start = Offset(0.0f, 0.0f),
            end = Offset(arrowLength, 0.0f),
            strokeWidth = currentArrow.strokeWidth.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = currentArrow.color,
            start = Offset(arrowLength, 0.0f),
            end = Offset(arrowLength - 15.0f, -5.0f),
            strokeWidth = currentArrow.strokeWidth.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = currentArrow.color,
            start = Offset(arrowLength - 15.0f, -5.0f),
            end = Offset(arrowLength - 15.0f, 5.0f),
            strokeWidth = currentArrow.strokeWidth.toPx(),
            cap = StrokeCap.Round
        )


        drawLine(
            color = currentArrow.color,
            start = Offset(arrowLength - 15.0f, 5.0f),
            end = Offset(arrowLength, 0.0f),
            strokeWidth = currentArrow.strokeWidth.toPx(),
            cap = StrokeCap.Round
        )
    }
}

data class Arrow(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
) {
    companion object {
        val Unspecified = Arrow(Offset.Unspecified, Offset.Unspecified, Color.Unspecified, Dp.Unspecified)
    }
}


@Composable
fun DrawingRectScreen() {
    val rectangles = remember {
        mutableStateListOf<Rect>()
    }

    var currentRectangle by remember { mutableStateOf(Rect.Unspecified) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset -> currentRectangle = Rect(offset, offset) },
                    onDragEnd = {
                        rectangles.add(currentRectangle)
                        currentRectangle = Rect.Unspecified
                    },
                    onDragCancel = {
                        currentRectangle = Rect.Unspecified
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()


                        currentRectangle = Rect(currentRectangle.start, change.position)
                    })
            }
    ) {
        rectangles.forEach { rect ->
            drawRect(
                color = rect.color,
                topLeft = rect.start,
                size = Size(rect.end.x-rect.start.x, rect.end.y-rect.start.y)
            )
        }
        if (currentRectangle != Rect.Unspecified) {
            drawRect(
                color = currentRectangle.color,
                topLeft = currentRectangle.start,
                size = Size(currentRectangle.end.x-currentRectangle.start.x, currentRectangle.end.y-currentRectangle.start.y)
            )
        }
    }
}

data class Rect(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
) {
    companion object {
        val Unspecified = Rect(Offset.Unspecified, Offset.Unspecified, Color.Unspecified, Dp.Unspecified)
    }
}

@Composable
fun DrawingStepsScreen() {
    val steps = remember {
        mutableStateListOf<Step>()
    }
    var stepCounter by remember { mutableStateOf(0) }

    var currentStep by remember { mutableStateOf(Step.Unspecified) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset -> currentStep = Step(offset, stepCounter+1) },
                    onDragEnd = {
                        steps.add(currentStep)
                        stepCounter+=1
                        currentStep = Step.Unspecified
                    },
                    onDragCancel = {
                        currentStep = Step.Unspecified
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()


                        currentStep = Step(change.position, stepCounter+1)
                    })
            }
    ) {
        steps.forEach { step ->
            drawCircle(
                color = Color.Green,
                radius = 30.0f,
                center = step.position
            )
        }
        if (currentStep != Step.Unspecified) {
            drawCircle(
                color = Color.Green,
                radius = 30.0f,
                center = currentStep.position
            )
        }
    }
}

data class Step(
    val position: Offset,
    val count: Int
){
    companion object {
        val Unspecified = Step(Offset.Unspecified, 0);
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
