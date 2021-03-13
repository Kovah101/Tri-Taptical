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
        if (oldCellID == cellID) {
            //add one to tapCount and add to cellID to cycle through
            tapCount++
        } else {
            tapCount = 0
        }
        oldCellID = cellID
        var lastTrueCellID = trueCellID
        trueCellID = cellID + tapCount

        Log.d("ButtonPress", "selected cell: $cellID")
        Log.d("ButtonPress", "selected segment: $trueCellID")
        Log.d("ButtonPress", "last segment: $lastTrueCellID")


        setSegmentColor(trueCellID, lastTrueCellID)
        // reset tapCount
        if (tapCount >= 2) {
            tapCount = -1
        }
    }
    // TODO old segment back to default
    fun setSegmentColor(cellID: Int, lastCellID: Int) {
        val playerOneColor = resources.getColor(R.color.playerOne)
        val playerTwoColor = resources.getColor(R.color.playerTwo)
        if (activePlayer == 1) {
            //set selected segment to p1 colour
            val currentView = findViewById<View>(locationIDs[cellID])
            currentView.setBackgroundColor(playerOneColor)


        } else if (activePlayer == 2) {
            // set selected segment to p2 colour
            val currentView = findViewById<View>(locationIDs[cellID])
            currentView.setBackgroundColor(playerTwoColor)

        }

    }

    fun confirmMove(view: View) {
        if (activePlayer == 1){
            activePlayer = 2
        } else {
            activePlayer = 1
        }
    }
}