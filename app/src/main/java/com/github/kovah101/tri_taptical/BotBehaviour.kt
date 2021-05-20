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

// hardBot tries to win, otherwise stops next person winning
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
    Log.d("Bot-Behaviour", "Hard-Bot: Move-$winningMove")
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
// does not check with own moves
// TODO finish complex priority method
private fun priorityMove(player: Int, confirmedMoves: IntArray): Int {
    var priorityMove = -1
    // base win conditions for each possible move
    val movePriorities =
        intArrayOf(7, 4, 7, 4, 5, 4, 7, 4, 7, 4, 5, 4, 5, 13, 5, 4, 5, 4, 7, 4, 7, 4, 5, 4, 7, 4, 7)

    var currentMovePriorities = movePriorities

    // adjust for opponents moves
    currentMovePriorities = adjustMovePriority(currentMovePriorities, confirmedMoves, player, -1)
    // adjust for own moves
    currentMovePriorities = adjustMovePriority(currentMovePriorities, confirmedMoves, player, 1)
    // remove taken moves
    currentMovePriorities = removeConfirmedMoves(currentMovePriorities, confirmedMoves)
    // pick out highest priority moves
    val bestMoves = pickBestMoves(currentMovePriorities)
    // randomly pick from best moves
    priorityMove = bestMoves.random()

    // middleMiddle square highest priority
    // middleMiddle used in 13/12 win conditions
    if (confirmedMoves[13] == 0) {
        priorityMove = 13
    }
    // empty outer or inner corners are next priority
    // used in 7 win conditions
    if (priorityMove == -1) {
        priorityMove = emptyCornerMoves(confirmedMoves)
    }
    // partial corners inner/outer are next priority
    // used in 6 win conditions
    if (priorityMove == -1) {
        priorityMove = partialCornerMoves(confirmedMoves)
    }
    // empty midline middles are next priority
    // used in 5 win conditions
    if (priorityMove == -1) {
        priorityMove = emptyMidlineMoves(confirmedMoves)
    }
    // partial midline middles or partial center are next priority
    // used in 4 win conditions
    if (priorityMove == -1) {
        priorityMove = partialMidlineOrCenterMoves(confirmedMoves)
    }
    // partial corner middles or midline inner/outer next priority
    // used in 3 win conditions
    if (priorityMove == -1) {
        priorityMove = middleCornerOrPartialMidline(confirmedMoves)
    }

    return priorityMove
}

// sets move priority to 0 for any confirmed moves - cant make already confirmed moves
private fun removeConfirmedMoves(
    currentMovePriorities: IntArray,
    confirmedMoves: IntArray
): IntArray {
    // cycle through each index of confirmed moves
    for (moveIndex in confirmedMoves.indices) {
        // if there is a confirmed move then set currentMovePriority to 0
        if (confirmedMoves[moveIndex] != 0) {
            currentMovePriorities[moveIndex] = 0
        }
    }
    return currentMovePriorities
}

// reduces priority for moves that are interfered by Opponents moves
// TODO finish function
private fun adjustMovePriority(
    currentMovePriorities: IntArray,
    confirmedMoves: IntArray,
    player: Int,
    amount: Int
): IntArray {
    // cycle through confirmed moves
    for (moveIndex in confirmedMoves.indices){
        // check for opponents move
        if(confirmedMoves[moveIndex] != 0 && confirmedMoves[moveIndex] != player){

        }
    }


    return currentMovePriorities
}

// finds highest priority and takes the index of these best moves
// TODO finish function
private fun pickBestMoves(currentMovePriorities: IntArray): IntArray {
    val bestMoves = intArrayOf(-1)

    return bestMoves
}


// checks for free corner moves and randomly selects one
private fun emptyCornerMoves(confirmedMoves: IntArray): Int {
    var cornerMove = -1
    val emptyCorners = arrayListOf<Int>()
    // check top left is empty
    if (confirmedMoves[0] == 0 && confirmedMoves[1] == 0 && confirmedMoves[2] == 0) {
        emptyCorners.add(0)
        emptyCorners.add(2)
    }
    // check top right is empty
    if (confirmedMoves[6] == 0 && confirmedMoves[7] == 0 && confirmedMoves[8] == 0) {
        emptyCorners.add(6)
        emptyCorners.add(8)
    }
    // check bottom left is empty
    if (confirmedMoves[18] == 0 && confirmedMoves[19] == 0 && confirmedMoves[20] == 0) {
        emptyCorners.add(18)
        emptyCorners.add(20)
    }
    // check bottom right is empty
    if (confirmedMoves[24] == 0 && confirmedMoves[25] == 0 && confirmedMoves[26] == 0) {
        emptyCorners.add(24)
        emptyCorners.add(26)
    }
    // pick a random available empty corner
    if (emptyCorners.isNotEmpty()) {
        cornerMove = emptyCorners.random()
    }

    return cornerMove
}

// checks for partial corner moves and randomly selects one
// TODO complete function
private fun partialCornerMoves(confirmedMoves: IntArray): Int {
    var partialCorner = -1

    return partialCorner
}

// checks for empty midline middle moves and randomly selects one
// TODO complete function
private fun emptyMidlineMoves(confirmedMoves: IntArray): Int {
    var emptyMidline = -1

    return emptyMidline
}

// checks for partial midline or center moves and randomly selects one
// TODO complete function
private fun partialMidlineOrCenterMoves(confirmedMoves: IntArray): Int {
    var partialMidlineOrCenter = -1

    return partialMidlineOrCenter
}

// checks for partial corner middle OR partial midline inner/outer moves and randomly selects one
// TODO complete function
private fun middleCornerOrPartialMidline(confirmedMoves: IntArray): Int {
    var midCornerOrPartialMidline = -1

    return midCornerOrPartialMidline
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