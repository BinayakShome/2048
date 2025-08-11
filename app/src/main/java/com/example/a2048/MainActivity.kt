package com.example.a2048

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFBBADA0)
                ) {
                    Game2048()
                }
            }
        }
    }
}

@Composable
fun Game2048() {
    var board by remember { mutableStateOf(newBoard()) }
    var score by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Main Game UI in Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("2048 Game", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Score: $score", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            for (row in board) {
                Row {
                    for (cell in row) {
                        Tile(cell)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Controls
            Row(horizontalArrangement = Arrangement.Center) {
                CircleButton("↑") {
                    val (b, s) = moveUp(board)
                    if (b != board) {
                        board = addRandomTile(b)
                        score += s
                        if (isGameOver(board)) {
                            Toast.makeText(context, "GAME OVER! Score: $score", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Center) {
                CircleButton("←") {
                    val (b, s) = moveLeft(board)
                    if (b != board) {
                        board = addRandomTile(b)
                        score += s
                        if (isGameOver(board)) {
                            Toast.makeText(context, "GAME OVER! Score: $score", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                Spacer(modifier = Modifier.width(80.dp))
                CircleButton("→") {
                    val (b, s) = moveRight(board)
                    if (b != board) {
                        board = addRandomTile(b)
                        score += s
                        if (isGameOver(board)) {
                            Toast.makeText(context, "GAME OVER! Score: $score", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Center) {
                CircleButton("↓") {
                    val (b, s) = moveDown(board)
                    if (b != board) {
                        board = addRandomTile(b)
                        score += s
                        if (isGameOver(board)) {
                            Toast.makeText(context, "GAME OVER! Score: $score", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        // ✅ Restart button in Box scope
        Button(
            onClick = {
                board = newBoard()
                score = 0
                Toast.makeText(context, "New Game", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(Color(0xFF8BC34A)),
            modifier = Modifier
                .align(Alignment.BottomEnd) // works now
                .padding(8.dp)
        ) {
            Text("Restart", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CircleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(64.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}


@Composable
fun Tile(value: Int) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .padding(4.dp)
            .background(tileColor(value), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (value != 0) {
            Text("$value", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

fun tileColor(value: Int): Color {
    return when (value) {
        0 -> Color(0xFFCDC1B4)
        2 -> Color(0xFFEEE4DA)
        4 -> Color(0xFFEDE0C8)
        8 -> Color(0xFFF2B179)
        16 -> Color(0xFFF59563)
        32 -> Color(0xFFF67C5F)
        64 -> Color(0xFFF65E3B)
        128 -> Color(0xFFEDCF72)
        256 -> Color(0xFFEDCC61)
        512 -> Color(0xFFEDC850)
        1024 -> Color(0xFFEDC53F)
        2048 -> Color(0xFFEDC22E)
        else -> Color.White
    }
}

fun newBoard(): List<List<Int>> {
    var board : List<List<Int>> = List(4) { MutableList(4) { 0 } }
    board = addRandomTile(board)
    board = addRandomTile(board)
    return board
}

fun addRandomTile(board: List<List<Int>>): List<List<Int>> {
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    for (r in 0..3) {
        for (c in 0..3) {
            if (board[r][c] == 0) emptyCells.add(Pair(r, c))
        }
    }
    if (emptyCells.isEmpty()) return board
    val (row, col) = emptyCells.random()
    val newBoard = board.map { it.toMutableList() }
    newBoard[row][col] = if (Random.nextInt(100) < 90) 2 else 4
    return newBoard
}

// Movement logic
fun moveLeft(board: List<List<Int>>): Pair<List<List<Int>>, Int> {
    var score = 0
    val newBoard = board.map { row ->
        val filtered = row.filter { it != 0 }.toMutableList()
        for (i in 0 until filtered.size - 1) {
            if (filtered[i] == filtered[i + 1]) {
                filtered[i] *= 2
                score += filtered[i]
                filtered[i + 1] = 0
            }
        }
        filtered.removeAll { it == 0 }
        while (filtered.size < 4) filtered.add(0)
        filtered
    }
    return Pair(newBoard, score)
}

fun moveRight(board: List<List<Int>>): Pair<List<List<Int>>, Int> {
    val reversed = board.map { it.reversed() }
    val (moved, score) = moveLeft(reversed)
    return Pair(moved.map { it.reversed() }, score)
}

fun moveUp(board: List<List<Int>>): Pair<List<List<Int>>, Int> {
    val transposed = (0..3).map { col -> (0..3).map { row -> board[row][col] } }
    val (moved, score) = moveLeft(transposed)
    val restored = (0..3).map { row -> (0..3).map { col -> moved[col][row] } }
    return Pair(restored, score)
}

fun moveDown(board: List<List<Int>>): Pair<List<List<Int>>, Int> {
    val transposed = (0..3).map { col -> (0..3).map { row -> board[row][col] } }
    val (moved, score) = moveRight(transposed)
    val restored = (0..3).map { row -> (0..3).map { col -> moved[col][row] } }
    return Pair(restored, score)
}

fun isGameOver(board: List<List<Int>>): Boolean {
    // Check for any empty cells
    if (board.any { row -> row.any { it == 0 } }) return false

    // Check for possible merges horizontally
    for (r in 0..3) {
        for (c in 0..2) {
            if (board[r][c] == board[r][c + 1]) return false
        }
    }

    // Check for possible merges vertically
    for (c in 0..3) {
        for (r in 0..2) {
            if (board[r][c] == board[r + 1][c]) return false
        }
    }

    return true // No moves left
}