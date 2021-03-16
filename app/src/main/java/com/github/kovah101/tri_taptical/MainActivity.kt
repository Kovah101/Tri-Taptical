package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val locationIDs = arrayOf(
        R.id.topLeft, R.id.topLeftMiddle, R.id.topLeftInner,
        R.id.topMiddle, R.id.topMiddleMiddle, R.id.topMiddleInner,
        R.id.topRight, R.id.topRightMiddle, R.id.topRightInner,
        R.id.middleLeft, R.id.middleLeftMiddle, R.id.middleLeftInner,
        R.id.middleMiddle, R.id.middleMiddleMiddle, R.id.middleMiddleInner,
        R.id.middleRight, R.id.middleRightMiddle, R.id.middleRightInner,
        R.id.bottomLeft, R.id.bottomLeftMiddle, R.id.bottomLeftInner,
        R.id.bottomMiddle, R.id.bottomMiddleMiddle, R.id.bottomMiddleInner,
        R.id.bottomRight, R.id.bottomRightMiddle, R.id.bottomRightInner
    )
    var tapCount = 0
    var oldCellID = -1
    var trueCellID = -1
    var activePlayer = 1
    //var activePlayerColor = 0
    var confirmedMoves = HashMap<Int, Int>()
    var winningMoves = arrayListOf<Int>()
    var selectedCell = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // activePlayerColor = resources.getColor(R.color.playerOne)
    }

    fun changeColor(view: View) {
        val selectedButton = view as Button
        var cellID = 0

        when (selectedButton.id) {
            R.id.topLeft -> cellID = 0
            R.id.topMiddle -> cellID = 3
            R.id.topRight -> cellID = 6
            R.id.middleLeft -> cellID = 9
            R.id.middleMiddle -> cellID = 12
            R.id.middleRight -> cellID = 15
            R.id.bottomLeft -> cellID = 18
            R.id.bottomMiddle -> cellID = 21
            R.id.bottomRight -> cellID = 24
        }


        // if new cell touched
        if (oldCellID != cellID) {
            selectedCell = cellID
        }

        // check if tapping the same cell, if so increment tapCount
        // add tapCount to cellID to find trueCellID
        if (oldCellID == cellID) {
            // add one to selected cell, if its too big break
            selectedCell++
            //add one to tapCount and add to cellID to cycle through
            tapCount++
        } else {
            tapCount = 0
        }
        if (selectedCell > cellID + 2) {
            selectedCell = cellID
        }

        // check if current cell is taken
        if (confirmedMoves.containsKey(selectedCell)) {
            // if at inner cell, reset to outer, otherwise step to next
            if (selectedCell == cellID + 2) {
                selectedCell = cellID
            } else {
                selectedCell++
            } // check if next cell is taken
            if (confirmedMoves.containsKey(selectedCell)) {
                // if at inner cell, reset to outer, otherwise step to next
                if (selectedCell == cellID + 2) {
                    selectedCell = cellID
                } else {
                    selectedCell++
                }// check if final cell is taken
                if (confirmedMoves.containsKey(selectedCell)) {
                    return
                }
            }
        }

        // store last tapped cellID
        oldCellID = cellID
        // store last tapped cell to be decoloured
        val lastTrueCellID = trueCellID
        //trueCellID = cellID + tapCount
        trueCellID = selectedCell

        Log.d("ButtonPress", "cID: $cellID, oldID: $oldCellID, sCell: $selectedCell")
        Log.d("ButtonPress", "tap count:$tapCount")



        setSegmentColor(trueCellID, lastTrueCellID)
        // reset tapCount
        if (tapCount >= 2) {
            tapCount = 0
        }
    }

    private fun setSegmentColor(cellID: Int, lastCellID: Int) {
        val playerOneColor = resources.getColor(R.color.playerOne)
        val playerTwoColor = resources.getColor(R.color.playerTwo)
        val white = resources.getColor(R.color.white)
        if (activePlayer == 1) {
            //set selected segment to p1 colour
            val currentView = findViewById<View>(locationIDs[cellID])
            currentView.setBackgroundColor(playerOneColor)
            // if last cell is not new cell or default
            // set last view back to white
            if (lastCellID != -1 && lastCellID != cellID) {
                val lastView = findViewById<View>(locationIDs[lastCellID])
                lastView.setBackgroundColor(white)
            }

        } else if (activePlayer == 2) {
            // set selected segment to p2 colour
            val currentView = findViewById<View>(locationIDs[cellID])
            currentView.setBackgroundColor(playerTwoColor)
            // if last cell is not new cell or default
            // set last view back to white
            if (lastCellID != -1 && lastCellID != cellID) {
                val lastView = findViewById<View>(locationIDs[lastCellID])
                lastView.setBackgroundColor(white)
            }
        }

    }

    fun confirmMove(view: View) {
        // add confirmed move to hash map with cell and active player
        confirmedMoves[trueCellID] = activePlayer
        // reset old cell ID
        oldCellID = -1
        // reset true cell ID
        trueCellID = -1
        // check for winner
        checkForWinner()

        // change player
        if (activePlayer == 1) {
            activePlayer = 2
            //activePlayerColor = resources.getColor(R.color.playerTwo)
        } else {
            activePlayer = 1
            //activePlayerColor = resources.getColor(R.color.playerOne)
        }
    }

    // TODO finish function
    private fun checkForWinner() {
        var winFlag = false

        // check for Spot winner
        if (spotWinner()) {
            winFlag = true
            Log.d("ButtonPress", "We have a spot winner")
        }
        // check for horizontal winner
        if (horizontalWinner()) {
            winFlag = true
            Log.d("ButtonPress", "We have a horizontal winner")
        }
        //check for vertical winner
        if (verticalWinner()) {
            winFlag = true
            Log.d("ButtonPress", "We have a vertical winner")
        }
        //check for diagonal winner
        if (diagonalWinner()) {
            winFlag = true
            Log.d("ButtonPress", "We have a diagonal winner")
        }


        // if there is a winner, add to scoreboard, show toast notification & reset board
        if (winFlag) {
            // increment score counter

            // show toast win notification
            Toast.makeText(this, "Player $activePlayer is the Winner!", Toast.LENGTH_LONG).show()

            // reset the board
            val white = resources.getColor(R.color.white)
            for (cell in locationIDs.indices) {
                val currentCell = findViewById<View>(locationIDs[cell])
                currentCell.setBackgroundColor(white)
            }
            // show winning moves
            val activeColor = pickColor(activePlayer)
            for (move in winningMoves){
                println("winning move: $move")
                val winningCell = findViewById<View>(locationIDs[move])
                winningCell.setBackgroundColor(activeColor)
            } // wait 2s
            // TODO clear and restart button
            //Thread.sleep(2000)
            // clear winning moves
            for (move in winningMoves){
                val winningCell = findViewById<View>(locationIDs[move])
                winningCell.setBackgroundColor(white)
            }
            // clear saved moves
            confirmedMoves.clear()
            winningMoves.clear()

        }
    }

    private fun pickColor(activePlayer: Int): Int{
        return when(activePlayer){
            1 -> resources.getColor(R.color.playerOne)
            2 -> resources.getColor(R.color.playerTwo)
            else -> R.color.white
        }
    }

    private fun spotWinner(): Boolean {
        // check for same cell winners
        for (i in 0..locationIDs.size step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 1] == activePlayer && confirmedMoves[i + 2] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 1)
                winningMoves.add(i + 2)
                return true
            }
        }
        return false
    }

    private fun horizontalWinner(): Boolean {
        // check for same size winning rows
        for (i in 0..18 step 9) {
            for (j in 0..2) {
                var k = i + j
                if (confirmedMoves[k] == activePlayer && confirmedMoves[k + 3] == activePlayer && confirmedMoves[k + 6] == activePlayer) {
                    return true
                }
            }
        }
        // check ascending size
        for (i in 2..20 step 9) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 2] == activePlayer && confirmedMoves[i + 4] == activePlayer) {
                return true
            }
        }
        // check descending size
        for (i in 0..18 step 9) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 4] == activePlayer && confirmedMoves[i + 8] == activePlayer) {
                return true
            }
        }
        return false
    }

    private fun verticalWinner(): Boolean {
        // check for same size winning columns
        for (i in 0..8) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 9] == activePlayer && confirmedMoves[i + 18] == activePlayer) {
                return true
            }
        }
        // check ascending size
        for (i in 2..8 step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 8] == activePlayer && confirmedMoves[i + 16] == activePlayer) {
                return true
            }
        }
        // check descending size
        for (i in 0..6 step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 10] == activePlayer && confirmedMoves[i + 20] == activePlayer) {
                return true
            }
        }
        return false
    }

    private fun diagonalWinner(): Boolean {
        // check topLeft to bottomRight
        for (i in 0..2 step 2) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[13] == activePlayer && confirmedMoves[26 - i] == activePlayer) {
                return true
            }
        }
        // check bottomLeft to topRight
        for (i in 18..20 step 2) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[13] == activePlayer && confirmedMoves[26 - i] == activePlayer) {
                return true
            }
        }
        return false
    }
}