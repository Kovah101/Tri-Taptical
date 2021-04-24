package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics


// TODO add autoPlay AI, possibly with difficulty?
class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private val locationIDs = arrayOf(
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

    private val scoreBoardIDs = arrayOf(
        R.id.p1ScoreBoard, R.id.p2ScoreBoard, R.id.p3ScoreBoard, R.id.p4ScoreBoard
    )

    private var oldCellID = -1
    private var trueCellID = -1
    private var maxPlayers = 4
    private var activePlayer = 1
    private var playerColors = arrayOf(0, 0, 0, 0)
    private var score = arrayOf(0, 0, 0, 0)
    private var confirmedMoves = HashMap<Int, Int>()
    private var winningMoves = arrayListOf<Int>()
    private var selectedCell = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // TODO startActivityFromResult depreciated look into it, could check intent content? could use registerForActivityResult
        //
        //
        val callingActivity = callingActivity
        Toast.makeText(this, callingActivity.toString(), Toast.LENGTH_SHORT).show()
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
        } // if selected cell is too big, reset to original
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

        // colour correct segment & decolour last picked
        setSegmentColor(trueCellID, lastTrueCellID)
    }

    private fun setSegmentColor(cellID: Int, lastCellID: Int) {
        val playerOneColor = resources.getColor(R.color.playerOne)
        val playerTwoColor = resources.getColor(R.color.playerTwo)
        val playerThreeColor = resources.getColor(R.color.playerThree)
        val playerFourColor = resources.getColor(R.color.playerFour)
        playerColors[0] = playerOneColor
        playerColors[1] = playerTwoColor
        playerColors[2] = playerThreeColor
        playerColors[3] = playerFourColor
        val white = resources.getColor(R.color.white)

        //set selected segment to p1 colour
        val currentView = findViewById<View>(locationIDs[cellID])
        currentView.setBackgroundColor(playerColors[activePlayer - 1])
        // if last cell is not new cell or default
        // set last view back to white
        if (lastCellID != -1 && lastCellID != cellID) {
            val lastView = findViewById<View>(locationIDs[lastCellID])
            lastView.setBackgroundColor(white)
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
        activePlayer++
        if (activePlayer > maxPlayers) {
            activePlayer = 1
        }
    }

    fun resetBoard(view: View) {
        // reset the board
        val white = resources.getColor(R.color.white)
        for (cell in locationIDs.indices) {
            val currentCell = findViewById<View>(locationIDs[cell])
            currentCell.setBackgroundColor(white)
        }

        // clear saved moves
        confirmedMoves.clear()
        winningMoves.clear()

        // hide reset button
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.visibility = View.GONE
        // reveal confirm button
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        confirmButton.visibility = View.VISIBLE

        // increment starting player
        activePlayer = startingPlayer(maxPlayers, score)

        // reset active player & scores if restarting
        if (view == findViewById(R.id.restartSettingsButton)) {
            Log.d("ButtonPress", "Restart Game")
            activePlayer = 1
            // reset score array
            for (scores in score.indices){
                score[scores] = 0
            }
            Log.d("ButtonPress", "${score[0]},${score[1]},${score[2]},${score[3]}")
            updateScore()
        }


    }

    private fun startingPlayer(maxPlayers: Int, score: Array<Int>): Int {
        val totalScore = score[0] + score[1] + score[2] + score[3]
        return (totalScore % maxPlayers) + 1

    }

    fun showSettings(view: View) {
        val settingsMenu = findViewById<View>(R.id.settingsMenu)
        settingsMenu.visibility = View.VISIBLE
        //Toast.makeText(this, "Settings coming Soon!", Toast.LENGTH_SHORT).show()
    }


    private fun checkForWinner() {
        var winFlag = false
        Log.d("ButtonPress", "Check Winner")
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
            // increment score counter & update score boards
            score[activePlayer - 1]++
            updateScore()
            Toast.makeText(this, "Player $activePlayer is the Winner!", Toast.LENGTH_SHORT).show()

            // set up blink animation
            val blinkAnimation = AlphaAnimation(1f, 0f)
            blinkAnimation.duration = 300
            blinkAnimation.interpolator = LinearInterpolator()
            blinkAnimation.repeatCount = 5
            blinkAnimation.repeatMode = Animation.RESTART
            // show winning moves by blinking

            for (move in winningMoves) {
                println("winning move: $move")
                val winningCell = findViewById<View>(locationIDs[move])
                winningCell.startAnimation(blinkAnimation)
            }

            // hide confirm button
            val confirmButton = findViewById<Button>(R.id.confirmButton)
            confirmButton.visibility = View.GONE
            // reveal reset button
            val resetButton = findViewById<Button>(R.id.resetButton)
            resetButton.visibility = View.VISIBLE

        }
    }

    private fun updateScore() {
        for (player in 1..score.size) {
            val scoreBoardID = scoreBoardIDs[player - 1]
            val scoreBoard = findViewById<TextView>(scoreBoardID)
            val scoreString = "P$player: ${score[player - 1]}"
            scoreBoard.text = scoreString
            scoreBoard.gravity = Gravity.CENTER
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
                val k = i + j
                if (confirmedMoves[k] == activePlayer && confirmedMoves[k + 3] == activePlayer && confirmedMoves[k + 6] == activePlayer) {
                    winningMoves.add(k)
                    winningMoves.add(k + 3)
                    winningMoves.add(k + 6)
                    return true
                }
            }
        }
        // check ascending size
        for (i in 2..20 step 9) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 2] == activePlayer && confirmedMoves[i + 4] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 2)
                winningMoves.add(i + 4)
                return true
            }
        }
        // check descending size
        for (i in 0..18 step 9) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 4] == activePlayer && confirmedMoves[i + 8] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 4)
                winningMoves.add(i + 8)
                return true
            }
        }
        return false
    }

    private fun verticalWinner(): Boolean {
        // check for same size winning columns
        for (i in 0..8) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 9] == activePlayer && confirmedMoves[i + 18] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 9)
                winningMoves.add(i + 18)
                return true
            }
        }
        // check ascending size
        for (i in 2..8 step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 8] == activePlayer && confirmedMoves[i + 16] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 8)
                winningMoves.add(i + 16)
                return true
            }
        }
        // check descending size
        for (i in 0..6 step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 10] == activePlayer && confirmedMoves[i + 20] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 10)
                winningMoves.add(i + 20)
                return true
            }
        }
        return false
    }

    private fun diagonalWinner(): Boolean {
        // check topLeft to bottomRight
        // same size
        for (i in 0..2) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 12] == activePlayer && confirmedMoves[i + 24] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 12)
                winningMoves.add(i + 24)
                return true
            }
        }
        // ascending or descending in size
        for (i in 0..2 step 2) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[13] == activePlayer && confirmedMoves[26 - i] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(13)
                winningMoves.add(26 - i)
                return true
            }
        }
        // check bottomLeft to topRight
        // same size
        for (i in 18..20) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i - 6] == activePlayer && confirmedMoves[i - 12] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i - 6)
                winningMoves.add(i - 12)
                return true
            }
        }
        // ascending or descending in size
        for (i in 18..20 step 2) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[13] == activePlayer && confirmedMoves[26 - i] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(13)
                winningMoves.add(26 - i)
                return true
            }
        }
        return false
    }

    // hide settings menu
    fun closeSettings(view: View) {
        val settingsMenu = findViewById<View>(R.id.settingsMenu)
        settingsMenu.visibility = View.GONE
    }
    // increase max player count
    fun incrementPlayers(view: View) {
        maxPlayers++
        if (maxPlayers > 4) {
            maxPlayers = 4
        }
        val playerNumberView = findViewById<TextView>(R.id.maxPlayerNumber)
        playerNumberView.text = maxPlayers.toString()
    }
    // or decrease max player count
    fun decrementPlayers(view: View) {
        maxPlayers--
        if (maxPlayers < 2) {
            maxPlayers = 2
        }
        val playerNumberView = findViewById<TextView>(R.id.maxPlayerNumber)
        playerNumberView.text = maxPlayers.toString()
    }
}