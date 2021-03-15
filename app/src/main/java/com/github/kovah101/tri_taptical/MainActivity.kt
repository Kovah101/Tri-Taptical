package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
    var confirmedMoves = HashMap<Int, Int>()
    var selectedCell = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        // change player
        checkForWinner()
        activePlayer = if (activePlayer == 1) {
            2
        } else {
            1
        }
    }

    // TODO finish function
    fun checkForWinner(){

    }
}