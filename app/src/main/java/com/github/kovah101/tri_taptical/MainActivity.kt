package com.github.kovah101.tri_taptical


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    // game types
    private val localGame = "LocalGame"
    private val onlineGame = "OnlineGame"
    private val botGame = "BotGame"

    // instance of database
    private val database = Firebase.database
    private val myRef = database.reference

    // online variables
    private var onlineGameName = ""
    private var myUsername = ""
    private var onlineFlag = false
    private var playerNames = arrayOf("", "P1", "P2", "P3", "P4")

    // bot variables
    private var botGameName = ""
    private var bots = arrayOf(0, 0, 0, 0)


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
    private var confirmedMoves = IntArray(27)
    private var winningMoves = arrayListOf<Int>()
    private var selectedCell = -1

    // TODO add notifications
    //  1-Generate token
    //  A- Open notifications - fix clicking behaviour & add login listener
    //  B- Closed notifications
    //  readMe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val loadingIntent = intent
        val gameType = loadingIntent.getStringExtra("gameType")
        when (gameType) {
            // setup for local game
            localGame -> {
                onlineFlag = false
                enableSettings(true)
                // increment starting player
                activePlayer = startingPlayer(maxPlayers, score)
                // show starting player
                highlightPlayer(activePlayer)

            }
            // setup for online game
            onlineGame -> {
                onlineFlag = true
                onlineGameName = loadingIntent.getStringExtra("gameName")!!
                myUsername = loadingIntent.getStringExtra("myUsername")!!
                playerNames = splitString(onlineGameName).toTypedArray()
                // -1 due to the unique ID in first space of array
                maxPlayers = playerNames.size - 1
                var botString = loadingIntent.getStringExtra("bots")!!
                botString = botString.dropLast(1)
                // convert string to array of strings then to integers
                bots = splitString(botString).map { it.toInt() }.toTypedArray()
                setupGame(playerNames)
                enableSettings(true)
                activePlayer = startingPlayer(maxPlayers, score)
                highlightPlayer(activePlayer)
                listenToGame()
                waitForBots(bots)
                waitYourTurn(playerNames, myUsername, activePlayer)
            }
            // setup bot game
            botGame -> {
                onlineFlag = false
                botGameName = loadingIntent.getStringExtra("gameName")!!
                myUsername = loadingIntent.getStringExtra("myUsername")!!
                var botString = loadingIntent.getStringExtra("bots")!!
                botString = botString.dropLast(1)
                // convert string to array of strings then to integers
                bots = splitString(botString).map { it.toInt() }.toTypedArray()
                playerNames = splitString(botGameName).toTypedArray()
                maxPlayers = playerNames.size - 1
                setupGame(playerNames)
                enableSettings(true)
                activePlayer = startingPlayer(maxPlayers, score)
                highlightPlayer(activePlayer)
                waitForBots(bots)

            }
            else -> {
                Toast.makeText(this, "How did you launch this?", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // takes player names, assigns max players, and fills in Player hubs
    private fun setupGame(playerNames: Array<String>) {
        maxPlayers = playerNames.size - 1
        // hide all hubs
        scoreBoardIDs.forEach { scoreboardID ->
            val scoreboard = findViewById<View>(scoreboardID)
            scoreboard.visibility = View.GONE
        }
        // show usable hubs and fill with player names
        for (player in 1..maxPlayers) {
            val scoreboard = findViewById<TextView>(scoreBoardIDs[player - 1])
            scoreboard.visibility = View.VISIBLE
            val scoreboardText = playerNames[player] + ":"
            scoreboard.text = scoreboardText
        }
    }

    // wait for bots go
    private fun waitForBots(bots: Array<Int>) {
        // if bots = 0 then human player so skip function
        if (bots[activePlayer - 1] == 0) {
            return
        } else {
            botTurn(bots[activePlayer - 1])
        }
    }

    // listen to specific game branch of database
    // when new move logged update screen and check for winner
    // activate or disable buttons if not your turn
    private fun listenToGame() {
        myRef.child("Games").child(onlineGameName)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val submittedMove = snapshot.value as String
                        val onlineMoveParts = splitString(submittedMove)
                        if (onlineMoveParts[0] != "RESET" && onlineMoveParts[0] != "RESTART") {
                            val onlineMove = onlineMoveParts[0].toInt()
                            val onlinePlayer = onlineMoveParts[1].toInt()
                            // take online move and update local board
                            setSegmentColor(onlineMove, -1, onlinePlayer)
                            // update local variables
                            confirmedMoves[onlineMove] = onlinePlayer
                            // check for winner - also increments to next player
                            val winner = checkForWinner()
                            if (!winner) {
                                // wait your turn
                                waitYourTurn(playerNames, myUsername, activePlayer)
                            }
                        }
                        if (onlineMoveParts[0] == "RESET") {
                            resetBoard()
                        }
                        if (onlineMoveParts[0] == "RESTART") {
                            restartGame()
                        }
                    } catch (ex: Exception) {

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(onlineGame, "gameListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Game.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // checks if it is your turn - ONLINE
    private fun waitYourTurn(playerNames: Array<String>, myUsername: String, currentPlayer: Int) {
        // if it is your turn - enable buttons
        Log.d("Reset", "Waiting: Player$currentPlayer ${playerNames[currentPlayer]}= $myUsername")
        if(onlineFlag) {
            if (playerNames[currentPlayer] == myUsername) {
                enablePlayerButtons(true)
            }
            // if not your turn disable buttons
            else {
                enablePlayerButtons(false)
            }
        }
    }

    // enable or disable player buttons if it is or isn't your turn
    private fun enablePlayerButtons(enable: Boolean) {
        // enable or disable player buttons
        for (i in 0..24 step 3) {
            val buttonID = locationIDs[i]
            val button = findViewById<Button>(buttonID)
            button.isClickable = enable
        }
        // enable or disable confirm button
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        confirmButton.isClickable = enable
        // enable or disable reset button
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.isClickable = enable
    }

    // enable or disable settings button
    private fun enableSettings(enable: Boolean) {
        val settingsButton = findViewById<ImageButton>(R.id.settings)
        settingsButton.isClickable = enable
    }

    // splits string into list around "@"
    private fun splitString(string: String): List<String> {
        return string.split("@")
    }

    // cycles through possible moves and lights up selected move
    fun changeColor(view: View) {
        val selectedButton = view as Button
        var cellID = 0
        //var trueCellID = -1

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
        if (confirmedMoves[selectedCell] != 0) {
            // if at inner cell, reset to outer, otherwise step to next
            if (selectedCell == cellID + 2) {
                selectedCell = cellID
            } else {
                selectedCell++
            } // check if next cell is taken
            if (confirmedMoves[selectedCell] != 0) {
                // if at inner cell, reset to outer, otherwise step to next
                if (selectedCell == cellID + 2) {
                    selectedCell = cellID
                } else {
                    selectedCell++
                }// check if final cell is taken
                if (confirmedMoves[selectedCell] != 0) {
                    return
                }
            }
        }

        // store last tapped cellID
        oldCellID = cellID
        // store last tapped cell to be discoloured - default is -1 if no cell to discolour
        val lastTrueCellID = trueCellID
        // change the true cell to the selected one
        trueCellID = selectedCell
        // create selected cell ID to be coloured
        val selectedCellID = selectedCell
        // colour correct segment & discolour last picked
        setSegmentColor(selectedCellID, lastTrueCellID, activePlayer)
    }

    // lights up the selected segment in the corresponding player color
    private fun setSegmentColor(cellID: Int, lastCellID: Int, activePlayer: Int) {
        val playerOneColor = resources.getColor(R.color.playerOne)
        val playerTwoColor = resources.getColor(R.color.playerTwo)
        val playerThreeColor = resources.getColor(R.color.playerThree)
        val playerFourColor = resources.getColor(R.color.playerFour)
        playerColors[0] = playerOneColor
        playerColors[1] = playerTwoColor
        playerColors[2] = playerThreeColor
        playerColors[3] = playerFourColor
        val white = resources.getColor(R.color.white)

        //set selected segment to active player colour
        val currentView = findViewById<View>(locationIDs[cellID])
        currentView.setBackgroundColor(playerColors[activePlayer - 1])
        // if last cell is not new cell or default
        // set last view back to white
        if (lastCellID != -1 && lastCellID != cellID) {
            val lastView = findViewById<View>(locationIDs[lastCellID])
            lastView.setBackgroundColor(white)
        }
    }

    // save move to memory, go to next player
    fun confirmButton(view: View) {
        confirmMove(selectedCell)
    }

    // confirms move to array, if online publishes the move to players
    private fun confirmMove(selectedCell: Int) {
        // check for draw
        if (!confirmedMoves.contains(0)) {
            dealWithDraw()
            return
        }
        // add confirmed move to hash map with cell and active player
        confirmedMoves[selectedCell] = activePlayer
        Log.d(onlineGame, "$selectedCell")
        // if online game then send confirmed move
        if (onlineFlag) {
            val onlineMove = "$selectedCell@$activePlayer"
            myRef.child("Games").child(onlineGameName).push().setValue(onlineMove)
        }
        // reset old cell ID
        oldCellID = -1
        // reset true cell ID
        trueCellID = -1

        //  if local game then check winner and go to next player
        if (!onlineFlag) {
            checkForWinner()
        }

    }

    private fun dealWithDraw() {
        Log.d("Draw", "Inside Draw condition")
        // display message and restart round
        Toast.makeText(
            this,
            "Its a Draw!",
            Toast.LENGTH_SHORT
        ).show()

        if (onlineFlag) {
            val reset = "RESET"
            myRef.child("Games").child(onlineGameName).push().setValue(reset)
        } else {
            resetBoard()
        }
    }

    // increments active player
    private fun nextPlayer() {
        activePlayer++
        if (activePlayer > maxPlayers) {
            activePlayer = 1
        }
        highlightPlayer(activePlayer)
    }

    // sends reset command to other players, restarts game board if restart
    fun resetButton(view: View) {

        // send reset to other online players
        if (view == findViewById(R.id.resetButton)) {
            // if online then send command, else local reset
            if (onlineFlag) {
                val reset = "RESET"
                myRef.child("Games").child(onlineGameName).push().setValue(reset)
            } else {
                resetBoard()
            }
        }

        // reset active player & scores if restarting
        if (view == findViewById(R.id.restartSettingsButton)) {
            // if online send command, else local restart
            if (onlineFlag) {
                val restart = "RESTART"
                myRef.child("Games").child(onlineGameName).push().setValue(restart)
                closeSettings(view)
            } else {
                Log.d("Restart", "Restart offline")
                closeSettings(view)
                restartGame()
            }

        }
    }

    // fully wipes scores and starts fresh
    // TODO restart bug midway through turns, bot only games?
    private fun restartGame() {
        // reset score array
        for (scores in score.indices) {
            score[scores] = 0
        }
        updateScore()
        resetBoard()
    }

    // restarts the game by clearing board and updating scores
    private fun resetBoard() {
        // reset the board
        val white = resources.getColor(R.color.white)
        for (cell in locationIDs.indices) {
            val currentCell = findViewById<View>(locationIDs[cell])
            currentCell.setBackgroundColor(white)
        }

        // clear saved moves
        confirmedMoves.fill(0)
        winningMoves.clear()

        // hide reset button
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.visibility = View.GONE
        // reveal confirm button
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        confirmButton.visibility = View.VISIBLE

        // increment starting player
        activePlayer = startingPlayer(maxPlayers, score)
        Log.d("Reset", "Restart: active player = $activePlayer")
        // show starting player
        highlightPlayer(activePlayer)

        // check whos turn it is
        waitYourTurn(playerNames, myUsername, activePlayer)

        // check if its a robots turn
        waitForBots(bots)
    }

    // calculates the starting player based on the score and max player number
    private fun startingPlayer(maxPlayers: Int, score: Array<Int>): Int {
        val totalScore = score[0] + score[1] + score[2] + score[3]
        return (totalScore % maxPlayers) + 1
    }

    //highlights the hub of current player
    private fun highlightPlayer(currentPlayer: Int) {
        val playerBorders = arrayOf(R.id.p1Border, R.id.p2Border, R.id.p3Border, R.id.p4Border)
        val playerColors = arrayOf(
            R.color.black,
            R.color.playerOne,
            R.color.playerTwo,
            R.color.playerThree,
            R.color.playerFour
        )

        for (playerNumber in playerBorders.indices) {
            val border = findViewById<View>(playerBorders[playerNumber])
            // current player highlighted
            if (playerNumber + 1 == currentPlayer) {
                val playerColour = resources.getColor(playerColors[currentPlayer])
                border.setBackgroundColor(playerColour)
            } else {
                border.setBackgroundColor(playerColors[0])
            }
        }
    }

    // reveals the settings menu
    fun showSettings(view: View) {
        val settingsMenu = findViewById<View>(R.id.settingsMenu)
        settingsMenu.visibility = View.VISIBLE
    }

    // checks for winner, if there is then blink the winning moves, update the scores and show reset
    private fun checkForWinner(): Boolean {
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
            // blink winning player border
            val borders = arrayOf(R.id.p1Border, R.id.p2Border, R.id.p3Border, R.id.p4Border)
            val winningBorder = findViewById<View>(borders[activePlayer-1])
            winningBorder.startAnimation(blinkAnimation)

            // hide confirm button
            val confirmButton = findViewById<Button>(R.id.confirmButton)
            confirmButton.visibility = View.GONE
            // reveal reset button
            val resetButton = findViewById<Button>(R.id.resetButton)
            resetButton.visibility = View.VISIBLE
        } else {
            nextPlayer()
            // wait for robots if its their turn
            waitForBots(bots)
        }

        return winFlag
    }

    // takes scores and displays them to scoreboards
    private fun updateScore() {
        for (player in 1..maxPlayers) {
            val scoreBoardID = scoreBoardIDs[player - 1]
            val scoreBoard = findViewById<TextView>(scoreBoardID)
            val scoreString = "${playerNames[player]}:${score[player - 1]}"
            scoreBoard.text = scoreString
            scoreBoard.gravity = Gravity.CENTER
        }

    }

    // checks for 3 in a row on the spot
    private fun spotWinner(): Boolean {
        // check for same cell winners
        for (i in 0 until locationIDs.size step 3) {
            if (confirmedMoves[i] == activePlayer && confirmedMoves[i + 1] == activePlayer && confirmedMoves[i + 2] == activePlayer) {
                winningMoves.add(i)
                winningMoves.add(i + 1)
                winningMoves.add(i + 2)
                return true
            }
        }
        return false
    }

    // checks for 3 in a row horizontally
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

    // checks for 3 in a row vertically
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

    // checks for 3 in a row diagonally
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
    }

    // or decrease max player count
    fun decrementPlayers(view: View) {
        maxPlayers--
        if (maxPlayers < 2) {
            maxPlayers = 2
        }
    }

    // bot functionality
    private fun botTurn(botDifficulty: Int) {
        // firstly disable buttons
        enablePlayerButtons(false)
        var botMove = 100
        when (botDifficulty) {
            1 -> botMove = easyBot(confirmedMoves)
            2 -> botMove = mediumBot(activePlayer, confirmedMoves, maxPlayers)
            3 -> botMove = hardBot(activePlayer, confirmedMoves, maxPlayers)
            else -> Toast.makeText(this, "What sort of bot is this??", Toast.LENGTH_SHORT).show()
        }

        val botDelay = 1500L

        Handler().postDelayed(
            {
                // colour chosen segment then save move
                setSegmentColor(botMove, -1, activePlayer)
                // enable reset button in case of bot win or draw on bot turn
                enablePlayerButtons(true)
                confirmMove(botMove)
            },
            botDelay
        )

    }

}