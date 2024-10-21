package ph.edu.auf.lazatin.jonathan.diceroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
        counts.containsValue(3) -> "Three of a Kind"
        counts.filterValues { it == 2 }.size == 2 -> "Two Pairs"
        counts.containsValue(2) -> "One Pair"
        else -> "No matches"
    }
}

@Composable
fun DiceRoller(modifier: Modifier = Modifier) {
    var diceValues by remember { mutableStateOf(List(6) { Dice(Random.nextInt(1, 7)) }) }
    var rollTrigger by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val result = evaluateDiceResult(diceValues)

    LaunchedEffect(rollTrigger) {
        showResult = false
        delay(600) // Delay for the duration of the dice roll animation
        showResult = true
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showResult) {
            Text(result, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        DiceGrid(diceValues, rollTrigger)
        Spacer(modifier = Modifier.height(16.dp))
        RollButton {
            diceValues = List(6) { Dice(Random.nextInt(1, 7)) }
            rollTrigger = !rollTrigger
        }
    }
}

@Composable
fun DiceGrid(diceValues: List<Dice>, rollTrigger: Boolean) {
    for (i in 0 until 2) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (j in 0 until 3) {
                val dice = diceValues[i * 3 + j]
                // Animate the dice value change
                val animatedValue by animateIntAsState(
                    targetValue = if (rollTrigger) dice.value else dice.value,
                    animationSpec = tween(durationMillis = 600)
                )
                Image(
                    painter = painterResource(id = getDiceImageResource(animatedValue)),
                    contentDescription = "Dice $animatedValue",
                    modifier = Modifier.size(100.dp)
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

@Preview(showBackground = true)
@Composable
fun DiceRollerPreview() {
    DiceRollTheme {
        DiceRoller()
    }
}