package ph.edu.auf.lazatin.jonathan.diceroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ph.edu.auf.lazatin.jonathan.diceroll.ui.theme.DiceRollTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiceRoller(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

fun getDiceImageResource(value: Int): Int {
    return when (value) {
        1 -> R.drawable.die_1
        2 -> R.drawable.die_2
        3 -> R.drawable.die_3
        4 -> R.drawable.die_4
        5 -> R.drawable.die_5
        6 -> R.drawable.die_6
        else -> R.drawable.die_1
    }
}

data class Dice(val value: Int)

fun evaluateDiceResult(diceValues: List<Dice>): String {
    val counts = diceValues.groupingBy { it.value }.eachCount()
    val uniqueValues = counts.keys.size

    return when {
        uniqueValues == 6 -> "Straight"
        counts.containsValue(6) -> "Six of a Kind"
        counts.containsValue(5) -> "Five of a Kind"
        counts.containsValue(4) -> "Four of a Kind"
        counts.containsValue(3) && counts.containsValue(2) -> "Full House"
        counts.containsValue(3) -> "Three of a kind"
        counts.filterValues { it == 2 }.size == 3 -> "Three Pairs"
        counts.filterValues { it == 2 }.size == 2 -> "Two Pairs"
        counts.containsValue(2) -> "One Pair"
        else -> "No matches"
    }
}

@Composable
fun DiceRoller(modifier: Modifier = Modifier) {
    var diceValues by remember { mutableStateOf(List(6) { Dice(Random.nextInt(1, 7)) }) }
    var rollTrigger by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    var rollCount by remember { mutableStateOf(0) }

    LaunchedEffect(rollTrigger) {
        delay(600)
        result = evaluateDiceResult(diceValues)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(result, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        DiceGrid(diceValues, rollCount)
        Spacer(modifier = Modifier.height(16.dp))
        RollButton {
            diceValues = List(6) { Dice(Random.nextInt(1, 7)) }
            rollTrigger = !rollTrigger
            rollCount++
        }
    }
}

@Composable
fun DiceGrid(diceValues: List<Dice>, rollCount: Int) {
    for (i in 0 until 2) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (j in 0 until 3) {
                val dice = diceValues[i * 3 + j]
                val animatedValue by animateFloatAsState(
                    targetValue = dice.value.toFloat(),
                    animationSpec = tween(durationMillis = 1000),
                    label = "dice_${i * 3 + j}_${rollCount}"
                )
                Image(
                    painter = painterResource(id = getDiceImageResource(animatedValue.toInt())),
                    contentDescription = "Dice ${animatedValue.toInt()}",
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(
                            rotationX = animatedValue * 360,
                            rotationY = animatedValue * 360
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RollButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        BasicText("Roll Dice")
    }
}