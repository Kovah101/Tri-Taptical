package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.online_lobby.*

class BotLobby : AppCompatActivity() {

    private var maxPlayers = 4
    private lateinit var myEmail: String
    private lateinit var myUsername: String
    private lateinit var hostUsername: String
    private lateinit var botGameName: String
    private var myPlayerNumber = 0
    private var playerNames = arrayOf("", "", "", "")
    private var myTotalPlayerNumbers = arrayOf(0, 0, 0, 0)
    private var botPlayers = arrayOf(0,0,0,0)

    // instance of database
    private val database = Firebase.database
    private val myRef = database.reference
    private val TAG = "BotLobby"

    // loading square ids
    private val loadingSquareIDs = arrayOf(
        R.id.p1BigSquare, R.id.p1MiddleSquare, R.id.p1SmallSquare,
        R.id.p2BigSquare, R.id.p2MiddleSquare, R.id.p2SmallSquare,
        R.id.p3BigSquare, R.id.p3MiddleSquare, R.id.p3SmallSquare,
        R.id.p4BigSquare, R.id.p4MiddleSquare, R.id.p4SmallSquare
    )

    // TODO: remove online database references


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bot_lobby)

        // extract info from login page
        val menuIntent = intent
        myUsername = menuIntent.getStringExtra("username")
        myEmail = myUsername

        //set username field to your actual username
        username.text = myUsername

        // clear the screen & invites
        clearEverything()

        Toast.makeText(applicationContext, "Started Bot Lobby!", Toast.LENGTH_SHORT).show()
    }

    // confirms player as human
    fun humanPlayer(view: View) {
        var playerNumber = 0
        var guestPlayer = ""

        // check if edit texts are empty if so show error and return
        // if not empty, store name, clear bot in that place and light up first two squares
        when (view) {
            p1Request -> {
                guestPlayer = player1Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 1
                playerNames[playerNumber-1] = guestPlayer
                botPlayers[playerNumber-1] = 0
                lightUpSquare(playerNumber, 1)
                lightUpSquare(playerNumber, 2)
            }
            p2Request -> {
                guestPlayer = player2Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 2
                playerNames[playerNumber-1] = guestPlayer
                botPlayers[playerNumber-1] = 0
                lightUpSquare(playerNumber, 1)
                lightUpSquare(playerNumber, 2)
            }
            p3Request -> {
                guestPlayer = player3Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 3
                playerNames[playerNumber-1] = guestPlayer
                botPlayers[playerNumber-1] = 0
                lightUpSquare(playerNumber, 1)
                lightUpSquare(playerNumber, 2)
            }
            p4Request -> {
                guestPlayer = player4Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 4
                playerNames[playerNumber-1] = guestPlayer
                botPlayers[playerNumber-1] = 0
                lightUpSquare(playerNumber, 1)
                lightUpSquare(playerNumber, 2)
            }
        }

    }

    // check if edit text is empty
    private fun checkIfEmpty(string: String): Boolean {
        return if (string.isEmpty()) {
            Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
            true
        } else {
            false
        }
    }

    // creates a bot of easy, medium or hard difficulty
    // TODO cycle through easy, medium or hard bots and add to Bot array, light up two stages
    fun generateBot(view: View) {
        var playerNumber = 0
        var guestPlayer = ""

            when (view) {
                p1Accept -> {
                    guestPlayer = player1Username.text.toString()
                    if (checkIfEmpty(guestPlayer)) return
                    playerNumber = 1
                    playerNames[playerNumber-1] = guestPlayer
                    cycleBots(playerNumber)
                    lightUpSquare(playerNumber, 1)
                    lightUpSquare(playerNumber, 2)

                }
                p2Accept -> {
                    guestPlayer = player1Username.text.toString()
                    if (checkIfEmpty(guestPlayer)) return
                    playerNumber = 2
                    playerNames[playerNumber-1] = guestPlayer
                    cycleBots(playerNumber)
                    lightUpSquare(playerNumber, 1)
                    lightUpSquare(playerNumber, 2)
                }
                p3Accept -> {
                    guestPlayer = player1Username.text.toString()
                    if (checkIfEmpty(guestPlayer)) return
                    playerNumber = 3
                    playerNames[playerNumber-1] = guestPlayer
                    cycleBots(playerNumber)
                    lightUpSquare(playerNumber, 1)
                    lightUpSquare(playerNumber, 2)
                }
                p4Accept -> {
                    guestPlayer = player1Username.text.toString()
                    if (checkIfEmpty(guestPlayer)) return
                    playerNumber = 4
                    playerNames[playerNumber-1] = guestPlayer
                    cycleBots(playerNumber)
                    lightUpSquare(playerNumber, 1)
                    lightUpSquare(playerNumber, 2)
                }
            }
    }

    // TODO cycle through easy, medium, hard bots and change button to display difficulty and add to array
    private fun cycleBots(playerNumber: Int){

    }


    fun clearButton(view: View) {
        clearEverything()
    }
    // clears your Request and Accept lists
    // colours loading squares black
    // TODO reset buttons to clickable = true
    private fun clearEverything() {
        // clear requests, accepts & games
        myRef.child("Users").child(myUsername).child("Requests").setValue(myEmail)
        myRef.child("Users").child(myUsername).child("Accepts").setValue(myEmail)
        myRef.child("Users").child(myUsername).child("Games").setValue(myEmail)
        // reset variables
        myPlayerNumber = 0
        myTotalPlayerNumbers = arrayOf(0, 0, 0, 0)
        playerNames = arrayOf("", "", "", "")
        // reset loading squares to black
        val black = R.color.black
        for (loadingSquareID in loadingSquareIDs) {
            val loadingSquare = findViewById<View>(loadingSquareID)
            loadingSquare.setBackgroundColor(resources.getColor(black))
        }
        // clear player inputs
        player1Username.text.clear()
        player2Username.text.clear()
        player3Username.text.clear()
        player4Username.text.clear()
    }

    // creates game name from all confirmed players
    // sends game name to each player
    fun confirmGameDetails(view: View) {
        Toast.makeText(
            this,
            "$maxPlayers with ${playerNames[0]},${playerNames[1]},${playerNames[2]},${playerNames[3]}",
            Toast.LENGTH_SHORT
        ).show()
        // return if not host or no accepted players
        when (maxPlayers) {
            2 -> if (playerNames[0].isEmpty() || playerNames[1].isEmpty()) {
                Toast.makeText(applicationContext, "No Players/Not Host", Toast.LENGTH_SHORT).show()
                return
            }
            3 -> if (playerNames[0].isEmpty() || playerNames[1].isEmpty() || playerNames[2].isEmpty()) {
                Toast.makeText(applicationContext, "No Players/Not Host", Toast.LENGTH_SHORT).show()
                return
            }
            4 -> if (playerNames[0].isEmpty() || playerNames[1].isEmpty() || playerNames[2].isEmpty() || playerNames[3].isEmpty()) {
                Toast.makeText(applicationContext, "No Players/Not Host", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // create game name from player names
        val gameName = createGameName(playerNames)
        Toast.makeText(applicationContext, gameName, Toast.LENGTH_SHORT).show()
        // send each player the game name
        for (name in playerNames) {
            // else creates spare Games branch
            if (name != "") {
                myRef.child("Users").child(name).child("Games").push().setValue(gameName)
                Log.d(TAG, name)
            }
        }
        // create the unique game on the Games branch
        myRef.child("Games").push().setValue(gameName)
    }

    // play button launches game passing the gameName in the intent
    // TODO launch activity with result
    fun playOnline(view: View) {
        val onlineGame = Intent(this, MainActivity::class.java)
        onlineGame.putExtra("gameType", "BotGame")
        onlineGame.putExtra("gameName", botGameName)
        onlineGame.putExtra("myUsername", myUsername)
        startActivity(onlineGame)
    }

    private fun fillPlayerHub(username: String, playerNumber: Int) {
        when (playerNumber) {
            1 -> player1Username.setText(username)
            2 -> player2Username.setText(username)
            3 -> player3Username.setText(username)
            4 -> player4Username.setText(username)
            else -> Log.d(TAG, "Error: player number incorrect")
        }
    }

    private fun lightUpSquare(playerNumber: Int, stageToColor: Int) {
        val playerColors =
            arrayOf(R.color.playerOne, R.color.playerTwo, R.color.playerThree, R.color.playerFour)
        when (playerNumber) {
            1 -> when (stageToColor) {
                1 -> p1BigSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                2 -> p1MiddleSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                3 -> p1SmallSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
            }
            2 -> when (stageToColor) {
                1 -> p2BigSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                2 -> p2MiddleSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                3 -> p2SmallSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
            }
            3 -> when (stageToColor) {
                1 -> p3BigSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                2 -> p3MiddleSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                3 -> p3SmallSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
            }
            4 -> when (stageToColor) {
                1 -> p4BigSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                2 -> p4MiddleSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
                3 -> p4SmallSquare.setBackgroundColor(resources.getColor(playerColors[playerNumber - 1]))
            }
        }
    }

    // disables username input field in UI
    private fun disableUsernameInput(playerNumber: Int) {
        // 0 is not editable
        when (playerNumber) {
            1 -> player1Username.inputType = 0
            2 -> player2Username.inputType = 0
            3 -> player3Username.inputType = 0
            4 -> player4Username.inputType = 0
        }
    }

    // splits string into list around "@"
    private fun splitString(string: String): List<String> {
        return string.split("@")
    }

    private fun sentButtonClicked() {
        Log.d("Test", "sentButton Clicked")
        p1Accept.isClickable = false
        p2Accept.isClickable = false
        p3Accept.isClickable = false
        p4Accept.isClickable = false
    }

    private fun acceptButtonClicked() {
        Log.d("Test", "acceptButton Clicked")
        p1Request.isClickable = false
        p2Request.isClickable = false
        p3Request.isClickable = false
        p4Request.isClickable = false
    }

    // increase max player count
    fun incrementPlayers(view: View) {
        Log.d("Test", "Button Clicked successfully")
        maxPlayers++
        if (maxPlayers > 4) {
            maxPlayers = 4
        }
        displayPlayerHubs(maxPlayers)

        val playerNumberView = findViewById<TextView>(R.id.maxPlayers)
        playerNumberView.text = maxPlayers.toString()
    }

    // or decrease max player count
    fun decrementPlayers(view: View) {
        Log.d("Test", "Button Clicked successfully")
        maxPlayers--
        if (maxPlayers < 2) {
            maxPlayers = 2
        }
        displayPlayerHubs(maxPlayers)

        val playerNumberView = findViewById<TextView>(R.id.maxPlayers)
        playerNumberView.text = maxPlayers.toString()
    }

    private fun displayPlayerHubs(maxPlayers: Int){
        val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
        val player4Hub = findViewById<ConstraintLayout>(R.id.p4Hub)

        when(maxPlayers){
            2 -> {
                player3Hub.visibility = View.GONE
                player4Hub.visibility = View.GONE
            }
            3 -> {
                player3Hub.visibility = View.VISIBLE
                player4Hub.visibility = View.GONE
            }
            4 -> {
                player3Hub.visibility = View.VISIBLE
                player4Hub.visibility = View.VISIBLE
            }
        }
    }

    // adds confirmed players names together to form game name
    // bug on missing players ?? e.g. 1 - 3 4
    // TODO fix missing player bug
    private fun createGameName(playerNames: Array<String>): String {
        // game name starts with unique timestamp
        var gameName = System.currentTimeMillis().toString()
        if (playerNames[0].isNotEmpty()) {
            gameName += "@"
            gameName += playerNames[0]
            if (playerNames[1].isNotEmpty()) {
                gameName += "@"
                gameName += playerNames[1]
                if (playerNames[2].isNotEmpty()) {
                    gameName += "@"
                    gameName += playerNames[2]
                    if (playerNames[3].isNotEmpty()) {
                        gameName += "@"
                        gameName += playerNames[3]
                    }
                }
            }
        }
        return gameName
    }
}