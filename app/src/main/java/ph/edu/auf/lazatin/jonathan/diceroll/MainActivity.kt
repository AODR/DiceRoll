package ph.edu.auf.lazatin.jonathan.diceroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
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
    var result by remember { mutableStateOf("") }
    var rollCount by remember { mutableStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }

    // Randomize the dice values during the animation
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            // Keep randomizing while animation is running
            val animationDuration = 1000L
            val randomizationInterval = 100L
            val animationEndTime = System.currentTimeMillis() + animationDuration

            while (System.currentTimeMillis() < animationEndTime) {
                diceValues = diceValues.map {
                    Dice(Random.nextInt(1, 7))
                }
                delay(randomizationInterval) // Delay between dice value updates
            }

            // Stop randomizing when the animation ends
            isAnimating = false
            result = evaluateDiceResult(diceValues)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(result, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        DiceGrid(diceValues, isAnimating)
        Spacer(modifier = Modifier.height(16.dp))
        RollButton {
            if (!isAnimating) {
                isAnimating = true // Start the animation and randomization
                rollCount++  // Trigger a new roll
            }
        }
    }
}

@Composable
fun DiceGrid(diceValues: List<Dice>, isAnimating: Boolean) {
    for (i in 0 until 2) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (j in 0 until 3) {
                val dice = diceValues[i * 3 + j]
                val rotation by animateFloatAsState(
                    targetValue = if (isAnimating) 360f else 0f,
                    animationSpec = if (isAnimating) tween(durationMillis = 1500) else tween(0),
                    label = "rotation_${i * 3 + j}_${dice.value}"
                )
                Image(
                    painter = painterResource(id = getDiceImageResource(dice.value)),
                    contentDescription = "Dice ${dice.value}",
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(
                            rotationX = rotation,
                            rotationY = rotation
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
