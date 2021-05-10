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
    var winningMove = -1
    var nextPlayer = activePlayer + 1
    if (nextPlayer > maxPlayers) {
        nextPlayer = 1
    }
    // try and win, otherwise stop next player from winning
    winningMove = winningMove(activePlayer, confirmedMoves)
    if (winningMove == -1) {
        winningMove = winningMove(nextPlayer, confirmedMoves)
    }
    // else pick a priority move
    if (winningMove == -1) {
        winningMove = priorityMove(activePlayer, confirmedMoves)
    }
    Log.d("Bot-Behaviour", "Hard-Bot!")
    return winningMove
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

// check to see which priority moves are left and select the highest priority move
private fun priorityMove(player: Int, confirmedMoves: IntArray): Int {
    var priorityMove = -1

    // middleMiddle square highest priority
    if (confirmedMoves[13] == 0) {
        priorityMove = 13
    }
    // outer or inner corners are next priority
    if (priorityMove == -1) {
        priorityMove = cornerMoves(confirmedMoves)
    }

    return priorityMove
}

// checks for free corner moves and randomly selects one
// TODO develop cornerMove priority system
private fun cornerMoves(confirmedMoves: IntArray): Int {
    var cornerMove = -1
    val allCornerMoves = intArrayOf(0, 2, 6, 8, 18, 20, 24, 26)
    var tlFlag = false
    var trFlag = false
    var blFlag = false
    var brFlag = false
    if (confirmedMoves[0] != 0 || confirmedMoves[2] != 0) {
        tlFlag = true
    }
    if (confirmedMoves[6] != 0 || confirmedMoves[8] != 0) {
        tlFlag = true
    }
    if (confirmedMoves[0] != 0 || confirmedMoves[2] != 0) {
        tlFlag = true
    }
    if (confirmedMoves[0] != 0 || confirmedMoves[2] != 0) {
        tlFlag = true
    }


    return cornerMove
}

// checks confirmed moves for a diagonal winner
private fun diagonalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1
    // check topLeft to bottomRight
    // check same size diagonals
    for (i in 0..2) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 12] == player && confirmedMoves[i + 24] == 0) {
            winningMove = i + 24
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 12] == 0 && confirmedMoves[i + 24] == player) {
            winningMove = i + 12
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 12] == player && confirmedMoves[i + 24] == player) {
            winningMove = i
        }
    }
    // check ascending or descending size diagonals
    for (i in 0..2 step 2) {
        if (confirmedMoves[i] == player && confirmedMoves[13] == player && confirmedMoves[26 - i] == 0) {
            winningMove = 26 - i
        } else if (confirmedMoves[i] == player && confirmedMoves[13] == 0 && confirmedMoves[26 - i] == player) {
            winningMove = 13
        } else if (confirmedMoves[i] == 0 && confirmedMoves[13] == player && confirmedMoves[26 - i] == player) {
            winningMove = i
        }
    }

    // check bottomLeft to topRight
    // check same size diagonals
    for (i in 18..20) {
        if (confirmedMoves[i] == player && confirmedMoves[i - 6] == player && confirmedMoves[i - 12] == 0) {
            winningMove = i - 12
        } else if (confirmedMoves[i] == player && confirmedMoves[i - 6] == 0 && confirmedMoves[i - 12] == player) {
            winningMove = i - 6
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i - 6] == player && confirmedMoves[i - 12] == player) {
            winningMove = i
        }
    }
    // check ascending or descending size diagonals
    for (i in 18..20 step 2) {
        if (confirmedMoves[i] == player && confirmedMoves[13] == player && confirmedMoves[26 - i] == 0) {
            winningMove = 26 - i
        } else if (confirmedMoves[i] == player && confirmedMoves[13] == 0 && confirmedMoves[26 - i] == player) {
            winningMove = 13
        } else if (confirmedMoves[i] == 0 && confirmedMoves[13] == player && confirmedMoves[26 - i] == player) {
            winningMove = i
        }
    }

    return winningMove
}

// checks confirmed moves for vertical winner
private fun verticalNearWin(player: Int, confirmedMoves: IntArray): Int {
    var winningMove = -1

    // check same size vertical
    for (i in 0..8) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 9] == player && confirmedMoves[i + 18] == 0) {
            winningMove = i + 18
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 9] == 0 && confirmedMoves[i + 18] == player) {
            winningMove = i + 9
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 9] == player && confirmedMoves[i + 18] == player) {
            winningMove = i
        }
    }

    // check ascending size vertical
    for (i in 2..8 step 3) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 8] == player && confirmedMoves[i + 16] == 0) {
            winningMove = i + 16
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 8] == 0 && confirmedMoves[i + 16] == player) {
            winningMove = i + 8
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 8] == player && confirmedMoves[i + 16] == player) {
            winningMove = i
        }
    }

    // check descending size vertical
    for (i in 0..6 step 3) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 10] == player && confirmedMoves[i + 20] == 0) {
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
    for (i in 0..18 step 9) {
        for (j in 0..2) {
            if (confirmedMoves[i + j] == player && confirmedMoves[i + j + 3] == player && confirmedMoves[i + j + 6] == 0) {
                winningMove = i + j + 6
            } else if (confirmedMoves[i + j] == player && confirmedMoves[i + j + 3] == 0 && confirmedMoves[i + j + 6] == player) {
                winningMove = i + j + 3
            } else if (confirmedMoves[i + j] == 0 && confirmedMoves[i + j + 3] == player && confirmedMoves[i + j + 6] == player) {
                winningMove = i + j
            }
        }
    }

    // check ascending size horizontal
    for (i in 2..20 step 9) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 2] == player && confirmedMoves[i + 4] == 0) {
            winningMove = i + 4
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 2] == 0 && confirmedMoves[i + 4] == player) {
            winningMove = i + 2
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 2] == player && confirmedMoves[i + 4] == player) {
            winningMove = i
        }
    }

    // check descending size horizontal
    for (i in 0..18 step 9) {
        if (confirmedMoves[i] == player && confirmedMoves[i + 4] == player && confirmedMoves[i + 8] == 0) {
            winningMove = i + 8
        } else if (confirmedMoves[i] == player && confirmedMoves[i + 4] == 0 && confirmedMoves[i + 8] == player) {
            winningMove = i + 4
        } else if (confirmedMoves[i] == 0 && confirmedMoves[i + 4] == player && confirmedMoves[i + 8] == player) {
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