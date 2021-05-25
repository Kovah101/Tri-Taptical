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
        winningMove = priorityMove(activePlayer, confirmedMoves, maxPlayers)
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
private fun priorityMove(activePlayer: Int, confirmedMoves: IntArray, maxPlayers: Int): Int {
    var priorityMove = -1
    // base win conditions for each possible move
    // each is x2 to weight opponents negative over own positives
    val movePriorities =
        intArrayOf(
            14,8,14,
            8,10,8,
            14,8,14,
            8,10,8,
            10,26,10,
            8,10,8,
            14,8,14,
            8,10,8,
            14,8,14
        )

    var currentMovePriorities = movePriorities
    // cycle through players adjusting +ve or -ve accordingly
    for (player in 1..maxPlayers){
        if (player == activePlayer){
            currentMovePriorities = adjustMovePriority(currentMovePriorities, confirmedMoves, player, 1)
        } else{
            currentMovePriorities = adjustMovePriority(currentMovePriorities, confirmedMoves, player, -2)
        }
    }
    // remove taken moves
    currentMovePriorities = removeConfirmedMoves(currentMovePriorities, confirmedMoves)
    // pick out highest priority moves
    val bestMoves = pickBestMoves(currentMovePriorities)
    // randomly pick from best moves
    priorityMove = bestMoves.random()

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
private fun adjustMovePriority(
    currentMovePriorities: IntArray,
    confirmedMoves: IntArray,
    player: Int,
    amount: Int
): IntArray {
    var mutableMovePriorities = currentMovePriorities
    // cycle through confirmed moves
    for (moveIndex in confirmedMoves.indices) {
        // check for opponents move
        if (confirmedMoves[moveIndex] != 0 && confirmedMoves[moveIndex] != player) {
            when (moveIndex) {
                // inside or outside corners
                0, 2, 6, 8, 18, 20, 24, 26 -> mutableMovePriorities =
                    iOCornerAdjustment(mutableMovePriorities, moveIndex, amount)
                // inside or outside midlines
                3, 5, 9, 11, 15, 17, 21, 23 -> mutableMovePriorities =
                    iOMidlineAdjustment(mutableMovePriorities, moveIndex, amount)
                // middle midlines
                4, 10, 16, 22 -> mutableMovePriorities =
                    midMidlineAdjustment(mutableMovePriorities, moveIndex, amount)
                // middle corners
                1, 7, 19, 25 -> mutableMovePriorities =
                    midCornerAdjustment(mutableMovePriorities, moveIndex, amount)
                // inside or outside center
                12, 14 -> mutableMovePriorities =
                    iOCenterAdjustment(mutableMovePriorities, moveIndex, amount)
                // mid center
                13 -> mutableMovePriorities =
                    midCenterAdjustment(mutableMovePriorities, moveIndex, amount)
            }
        }
    }

    return mutableMovePriorities
}

// finds highest priority and takes the index of these best moves
private fun pickBestMoves(currentMovePriorities: IntArray): ArrayList<Int> {
    val bestMoves = arrayListOf<Int>()
    val maxPriority = currentMovePriorities.maxOrNull()

    // fill best moves with the indices of the highest priority moves
    for (moveIndex in currentMovePriorities.indices) {
        if (currentMovePriorities[moveIndex] == maxPriority) {
            bestMoves.add(moveIndex)
        }
    }
    return bestMoves
}

// adjusts priorities for inner, middle & outer in same section
private fun spotAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    // find the section
    val section = moveIndex / 3
    val modPattern = intArrayOf(modifier, modifier, modifier)

    // adjust inner, middle and outer priorities of that section
    return sectionAdjust(movePriorities, section, modPattern)
}

// adjusts priorities for inner or outer corner moves
private fun iOCornerAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    val section = moveIndex / 3
    val corners = intArrayOf(0, 2, 6, 8)
    // generate adjacent and far mod patterns
    val farModPattern = intArrayOf(modifier, 0, modifier)
    var adjModPattern = intArrayOf(0, modifier, modifier)
    if (moveIndex % 3 == 0) {
        adjModPattern = intArrayOf(modifier, modifier, 0)
    }
    // adjust adjacent squares
    // always adjust middle & own section
    var adjustedPriorities = sectionAdjust(movePriorities, 4, adjModPattern)
    adjustedPriorities = spotAdjustment(adjustedPriorities, moveIndex, modifier)
    // adjust other adjacent depending on top or bottom section
    // top section adjustments
    if (section == 0 || section == 2) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, 1, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 3, adjModPattern)
    }
    // bottom section adjustments
    if (section == 6 || section == 8) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, 7, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section - 3, adjModPattern)
    }
    // adjust all corner squares
    for (corner in corners) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, corner, farModPattern)
    }

    return adjustedPriorities
}

// adjusts priorities for inner or outer midline moves
private fun iOMidlineAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    val section = moveIndex / 3
    // adjacent, far & mid mod patterns
    val farModPattern = intArrayOf(modifier, 0, modifier)
    var adjModPattern = intArrayOf(0, 0, modifier)
    var midModPattern = intArrayOf(0, modifier, modifier)
    if (moveIndex % 3 == 0) {
        adjModPattern = intArrayOf(modifier, 0, 0)
        midModPattern = intArrayOf(modifier, modifier, 0)
    }
    // adjust adjacent squares
    // always adjust middle & own sections
    var adjustedPriorities = sectionAdjust(movePriorities, 4, midModPattern)
    adjustedPriorities = spotAdjustment(adjustedPriorities, moveIndex, modifier)
    // adjust adjacent squares
    if (section == 1 || section == 7) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, section - 1, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 1, adjModPattern)
    }
    if (section == 3 || section == 5) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 3, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section - 3, adjModPattern)
    }
    // adjust opposite square
    when (section) {
        1 -> adjustedPriorities = sectionAdjust(adjustedPriorities, 7, farModPattern)
        3 -> adjustedPriorities = sectionAdjust(adjustedPriorities, 5, farModPattern)
        5 -> adjustedPriorities = sectionAdjust(adjustedPriorities, 3, farModPattern)
        7 -> adjustedPriorities = sectionAdjust(adjustedPriorities, 1, farModPattern)
    }

    return adjustedPriorities
}

// adjusts priorities for middle midline moves
private fun midMidlineAdjustment(
    movePriorities: IntArray,
    moveIndex: Int,
    modifier: Int
): IntArray {
    val section = moveIndex / 3
    // adjacent and far mod patterns
    val adjModPattern = intArrayOf(modifier, modifier, modifier)
    val farModPattern = intArrayOf(0, modifier, 0)
    var adjustedPriorities = IntArray(27)
    // adjust squares depending on top/bottom
    if (section == 1 || section == 7) {
        adjustedPriorities = sectionAdjust(movePriorities, section - 1, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 1, adjModPattern)
        for (square in 1..7 step 3) {
            adjustedPriorities = sectionAdjust(adjustedPriorities, square, farModPattern)
        }
    }// or adjust squares depending on left/right
    if (section == 3 || section == 5) {
        adjustedPriorities = sectionAdjust(movePriorities, section - 3, adjModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 3, adjModPattern)
        for (square in 3..5) {
            adjustedPriorities = sectionAdjust(adjustedPriorities, square, farModPattern)
        }
    }

    return adjustedPriorities
}

// adjusts priorities for middle corner moves
private fun midCornerAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    val section = moveIndex / 3
    // self and other mod pattern
    val selfModPattern = intArrayOf(modifier, modifier, modifier)
    val otherModPattern = intArrayOf(0, modifier, 0)
    // adjust self section, all corners, center and adjacent
    var adjustedPriorities = sectionAdjust(movePriorities, section, selfModPattern)
    for (sector in 0..8 step 2) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, sector, otherModPattern)
    }
    if (section == 0 || section == 2) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, 1, otherModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section + 3, otherModPattern)
    }
    if (section == 6 || section == 8) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, 7, otherModPattern)
        adjustedPriorities = sectionAdjust(adjustedPriorities, section - 3, otherModPattern)
    }

    return adjustedPriorities
}

// adjusts priorities for inner/outer center moves
private fun iOCenterAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    val section = moveIndex / 3
    // self and other mod pattern
    val selfModPattern = intArrayOf(modifier, modifier, modifier)
    var otherModPattern = intArrayOf(0, 0, modifier)
    if (moveIndex % 3 == 0) {
        otherModPattern = intArrayOf(modifier, 0, 0)
    }
    // adjust self and all other sections
    var adjustedPriorities = sectionAdjust(movePriorities, section, selfModPattern)
    for (square in 0..8) {
        adjustedPriorities = sectionAdjust(adjustedPriorities, square, otherModPattern)
    }

    return adjustedPriorities
}

// adjusts priorities for middle center move
private fun midCenterAdjustment(movePriorities: IntArray, moveIndex: Int, modifier: Int): IntArray {
    var adjustedPriorities = movePriorities
    val modPattern = intArrayOf(modifier, modifier, modifier)
    // cycle through all sections applying modification
    if (moveIndex == 14) {
        for (section in 0..8) {
            adjustedPriorities = sectionAdjust(adjustedPriorities, section, modPattern)
        }
    }
    return adjustedPriorities
}

// generic priority adjust function for a given section
private fun sectionAdjust(movePriorities: IntArray, section: Int, modPattern: IntArray): IntArray {
    for (index in 0..2) {
        movePriorities[section + index] += modPattern[index]
    }

    return movePriorities
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