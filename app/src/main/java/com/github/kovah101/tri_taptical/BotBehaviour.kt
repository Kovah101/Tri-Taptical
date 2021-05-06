package com.github.kovah101.tri_taptical

import android.util.Log
import android.widget.Toast

// TODO finish bot behaviour then add notifications
// easyBot picks a random square and lights up
fun easyBot(activePlayer: Int, confirmedMoves: HashMap<Int, Int>): Int {
    Log.d("Bot-Behaviour", "Easy-Bot!")
    return 5
}

// mediumBot tries to stop winners, otherwise picks randoms
fun mediumBot(activePlayer: Int, confirmedMoves: HashMap<Int, Int>): Int {
    Log.d("Bot-Behaviour", "Medium-Bot!")
    return 12
}

// hardBot tries to stop next person winning, otherwise tries to win
fun hardBot(activePlayer: Int, confirmedMoves: HashMap<Int, Int>): Int {
    Log.d("Bot-Behaviour", "Hard-Bot!")
    return 20
}