package com.github.kovah101.tri_taptical

import android.util.Log

// TODO finish bot behaviour then add notifications
// easyBot picks a random square and lights up
fun easyBot(confirmedMoves: IntArray): Int {

    var randomMove = (0..26).random()
    while (confirmedMoves[randomMove] != 0) {
        randomMove = (0..26).random()
    }
    Log.d("Bot-Behaviour", "Easy-Bot: Move-$randomMove")
    return randomMove
}

// mediumBot tries to stop winners, otherwise picks randoms
fun mediumBot(activePlayer: Int, confirmedMoves: IntArray, maxPlayers: Int): Int {
    var stoppingMove = -1
    var nextPlayer = activePlayer
    // win, otherwise stop next player from winning
    checkEachPlayer@ for (player in 1..maxPlayers) {
        stoppingMove = winningMove(nextPlayer, confirmedMoves)
        // viable move so break
        if (stoppingMove != -1) {
            break@checkEachPlayer
        }
        // else check next player
        nextPlayer++
        if (nextPlayer > maxPlayers) {
            nextPlayer = 1
        }
    }
    // if no move still selected then go random
    if (stoppingMove == -1) {
        stoppingMove = easyBot(confirmedMoves)
    }
    Log.d("Bot-Behaviour", "Medium-Bot: Move-$stoppingMove")
    return stoppingMove
}

// hardBot tries to stop next person winning, otherwise tries to win
fun hardBot(activePlayer: Int, confirmedMoves: IntArray, maxPlayers: Int): Int {
    Log.d("Bot-Behaviour", "Hard-Bot!")
    return 20
}

// check if player has nearly won and select their winning move
private fun winningMove(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    // check spot then horizontal then vertical then diagonal for winning move
    winningMove = spotNearWin(player, confirmedMoves)
    if (winningMove == -1) {
        winningMove = horizontalNearWin(player, confirmedMoves)
        if (winningMove == -1) {
            winningMove = verticalNearWin(player, confirmedMoves)
            if (winningMove == -1) {
                winningMove = diagonalNearWin(player, confirmedMoves)
            }
        }
    }
    return winningMove
}

// checks confirmed moves for a diagonal winner
private fun diagonalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    return winningMove
}

// checks confirmed moves for vertical winner
private fun verticalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    return winningMove
}

// checks confirmed moves for horizontal winner
private fun horizontalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    return winningMove
}

// checks confirmed moves for spot winner
private fun spotNearWin(player: Int, confirmedMoves: IntArray): Int {
 val locations = 26
 var winningMove = -1
//    for (i in 0..locations step 3){
//
//    }
    return winningMove
}