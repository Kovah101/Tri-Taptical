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
    // check topLeft to bottomRight
    // check same size diagonals


    return winningMove
}

// checks confirmed moves for vertical winner
private fun verticalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    // check same size vertical
    for (i in 0..8){
        if (confirmedMoves[i] == player && confirmedMoves[i + 9] == player && confirmedMoves[i + 18] == 0) {
            winningMove = i + 18
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 9] == 0 && confirmedMoves[i + 18] == player) {
            winningMove = i + 9
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 9] == player && confirmedMoves[i + 18] == player) {
            winningMove = i
        }
    }

    // check ascending size vertical
    for (i in 2..8 step 3){
        if (confirmedMoves[i] == player && confirmedMoves[i + 8] == player && confirmedMoves[i + 16] == 0) {
            winningMove = i + 16
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 8] == 0 && confirmedMoves[i + 16] == player) {
            winningMove = i + 8
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 8] == player && confirmedMoves[i + 16] == player) {
            winningMove = i
        }
    }

    // check descending size vertical
    for (i in 0..6 step 3){
        if (confirmedMoves[i] == player && confirmedMoves[i + 10 ] == player && confirmedMoves[i + 20] == 0) {
            winningMove = i + 20
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 10] == 0 && confirmedMoves[i + 20] == player) {
            winningMove = i + 10
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 10] == player && confirmedMoves[i + 20] == player) {
            winningMove = i
        }
    }

    return winningMove
}

// checks confirmed moves for horizontal winner
private fun horizontalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    // check same size horizontal
    for (i in 0..18 step 9){
        for (j in 0..2){
            if (confirmedMoves[i+j] == player && confirmedMoves[i+j+3] == player && confirmedMoves[i+j+6] == 0){
                winningMove = i+j+6
            } else if (confirmedMoves[i+j] == player && confirmedMoves[i+j+3] == 0 && confirmedMoves[i+j+6] == player){
                winningMove = i+j+3
            }else if (confirmedMoves[i+j] == 0 && confirmedMoves[i+j+3] == player && confirmedMoves[i+j+6] == player){
                winningMove = i+j
            }
        }
    }

    // check ascending size horizontal
    for (i in 2..20 step 9){
        if (confirmedMoves[i] == player && confirmedMoves[i+2] == player && confirmedMoves[i+4] == 0){
            winningMove = i+4
        } else if (confirmedMoves[i] == player && confirmedMoves[i+2] == 0 && confirmedMoves[i+4] == player){
            winningMove = i+2
        }else if (confirmedMoves[i] == 0 && confirmedMoves[i+2] == player && confirmedMoves[i+4] == player){
            winningMove = i
        }
    }

    // check descending size horizontal
    for (i in 0..18 step 9){
        if (confirmedMoves[i] == player && confirmedMoves[i+4] == player && confirmedMoves[i+8] == 0){
            winningMove = i+8
        } else if (confirmedMoves[i] == player && confirmedMoves[i+4] == 0 && confirmedMoves[i+8] == player){
            winningMove = i+4
        }else if (confirmedMoves[i] == 0 && confirmedMoves[i+4] == player && confirmedMoves[i+8] == player){
            winningMove = i
        }
    }

    return winningMove
}

// checks confirmed moves for spot winner
private fun spotNearWin(player: Int, confirmedMoves: IntArray): Int {
    val locations = confirmedMoves.size
    var winningMove = -1
    // check each square
    for (i in 0 until locations step 3) {
        // check each combo
        if (confirmedMoves[i] == player && confirmedMoves[i + 1] == player && confirmedMoves[i + 2] == 0) {
            winningMove = i + 2
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 1] == 0 && confirmedMoves[i + 2] == player) {
            winningMove = i + 1
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 1] == player && confirmedMoves[i + 2] == player) {
            winningMove = i
        }
    }
    return winningMove
}