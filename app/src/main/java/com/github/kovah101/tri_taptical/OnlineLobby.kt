package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bot_lobby.*
import kotlinx.android.synthetic.main.online_lobby.*
import kotlinx.android.synthetic.main.online_lobby.p1BigSquare
import kotlinx.android.synthetic.main.online_lobby.p1MiddleSquare
import kotlinx.android.synthetic.main.online_lobby.p1SmallSquare
import kotlinx.android.synthetic.main.online_lobby.p2BigSquare
import kotlinx.android.synthetic.main.online_lobby.p2MiddleSquare
import kotlinx.android.synthetic.main.online_lobby.p2SmallSquare
import kotlinx.android.synthetic.main.online_lobby.p3BigSquare
import kotlinx.android.synthetic.main.online_lobby.p3MiddleSquare
import kotlinx.android.synthetic.main.online_lobby.p3SmallSquare
import kotlinx.android.synthetic.main.online_lobby.p4BigSquare
import kotlinx.android.synthetic.main.online_lobby.p4MiddleSquare
import kotlinx.android.synthetic.main.online_lobby.p4SmallSquare
import kotlinx.android.synthetic.main.online_lobby.player1Username
import kotlinx.android.synthetic.main.online_lobby.player2Username
import kotlinx.android.synthetic.main.online_lobby.player3Username
import kotlinx.android.synthetic.main.online_lobby.player4Username
import kotlinx.android.synthetic.main.online_lobby.username

class OnlineLobby : AppCompatActivity() {

    private var maxPlayers = 4
    private lateinit var myEmail: String
    private lateinit var myID: String
    private lateinit var myUsername: String
    private lateinit var hostUsername: String
    private lateinit var onlineGameName: String
    private lateinit var bots: String
    private var myPlayerNumber = 0
    private var playerNames = arrayOf("", "", "", "")
    private var myTotalPlayerNumbers = arrayOf(0, 0, 0, 0)
    private var botPlayers = arrayOf(0, 0, 0, 0)

    // instance of database
    private val database = Firebase.database
    private val myRef = database.reference
    private val TAG = "OnlineLobby"

    // loading square ids
    private val loadingSquareIDs = arrayOf(
        R.id.p1BigSquare, R.id.p1MiddleSquare, R.id.p1SmallSquare,
        R.id.p2BigSquare, R.id.p2MiddleSquare, R.id.p2SmallSquare,
        R.id.p3BigSquare, R.id.p3MiddleSquare, R.id.p3SmallSquare,
        R.id.p4BigSquare, R.id.p4MiddleSquare, R.id.p4SmallSquare
    )

    // TODO: fix bug - when playing with self and you do not send-accept each player in turn, only last invite is logged - have p1Invite, p3Accept variables from switch case structure

    // TODO - disable play button till host has clicked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_lobby)



        // extract info from login page
        val intent = intent
        // not notification - intent from menu
        if (!intent.getBooleanExtra("Notification", false)){
            myEmail = intent.getStringExtra("email").toString()
            myID = intent.getStringExtra("userID").toString()
            myUsername = intent.getStringExtra("username").toString()
            // clear the screen & invites
            clearEverything()
        } else {
            // intent from notification
                //Log.d(TAG, "Intent from Notification")
            myUsername = intent.getStringExtra("username").toString()
            myEmail = intent.getStringExtra("email").toString()
            val myPlayerNumber = intent.getIntExtra("playerNumber", 1)
            Log.d(TAG, "about to fill player hub")
            fillPlayerHub(myUsername, myPlayerNumber)
        }


        //set username field to your actual username
        username.text = myUsername

        // set up request listeners on your own Requests branch of database
        listenForInvites()

        // set up accept listeners on your own Accepts branch of database
        listenForAccepts()

        // set up game listeners on your own Games branch of the database
        listenForGames()

        // create notification channel
        val notifyMe = Notifications()
        notifyMe.createChannel(applicationContext)


        // if Send button clicks then disable Accept as player is host
//        listOf(p1Request, p2Request, p3Request, p4Request).forEach { button ->
//            button.setOnClickListener { sentButtonClicked() }
//        }

        // if Accept button clicks then disable Send as player is guest
//        listOf(p1Accept, p2Accept, p3Accept, p4Accept).forEach {
//            it.setOnClickListener { acceptButtonClicked() }
//        }
    }

    // send play request with hosts name and guest player number
    fun sendRequest(view: View) {
        var playerNumber = 0
        var guestPlayer = ""

        // check if edit texts are empty
        when (view) {
            p1Request -> {
                guestPlayer = player1Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 1
                humanRequest(playerNumber, view)
            }
            p2Request -> {
                guestPlayer = player2Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 2
                humanRequest(playerNumber, view)
            }
            p3Request -> {
                guestPlayer = player3Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 3
                humanRequest(playerNumber, view)
            }
            p4Request -> {
                guestPlayer = player4Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 4
                humanRequest(playerNumber, view)
            }
        }
        myRef.child("Users").child(guestPlayer).child("Requests").push()
            .setValue("$myUsername@$playerNumber")

    }

    // remove any bot info and light up appropriate loading square & button
    private fun humanRequest(playerNumber: Int, view: View) {
        botPlayers[playerNumber - 1] = 0
        renameBotButton(playerNumber)
        resetLoadingSquares(playerNumber)
        lightUpSquare(playerNumber, 1)
        resetButtonColor(view)
        recolorButton(view)
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

    // send accept invite back to host with confirmed name and lock edit texts
    // accept listener will lock in
    // lights up 2nd loading square
    fun acceptRequest(view: View) {
        if (myPlayerNumber != 0) {
            when (view) {
                p1Accept -> acceptActions(1, view)
                p2Accept -> acceptActions(2, view)
                p3Accept -> acceptActions(3, view)
                p4Accept -> acceptActions(4, view)
            }
            myRef.child("Users").child(hostUsername).child("Accepts").push()
                .setValue("$myUsername@$myPlayerNumber")
        } else {
            Toast.makeText(
                applicationContext, "You have no requests to accept", Toast.LENGTH_SHORT
            ).show()
        }
    }

    // lights up appropriate loading square and button
    private fun acceptActions(playerNumber: Int, view: View) {
        botPlayers[playerNumber - 1] = 0
        renameBotButton(playerNumber)
        resetLoadingSquares(playerNumber)
        lightUpSquare(playerNumber, 2)
        resetButtonColor(view)
        recolorButton(view)

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
        botPlayers = arrayOf(0, 0, 0, 0)
        // reset loading squares to black
        // reset loading squares to black
        resetLoadingSquares(0)
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
    fun playOnline(view: View) {
        // create bot string to pass on
        bots = createBotString(botPlayers)

        val onlineGame = Intent(this, MainActivity::class.java)
        onlineGame.putExtra("gameType", "OnlineGame")
        onlineGame.putExtra("gameName", onlineGameName)
        onlineGame.putExtra("myUsername", myUsername)
        onlineGame.putExtra("bots", bots)

        startActivity(onlineGame)
        finish()
    }

    // listen to your own requests in database
    private fun listenForInvites() {
        myRef.child("Users").child(myUsername).child("Requests")
            .addChildEventListener(object : ChildEventListener {
                // get latest request
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val requestValue = snapshot.value as String
                        val requestParts = splitString(requestValue)
                        myPlayerNumber = requestParts[1].toInt()
                        hostUsername = requestParts[0]
                        fillPlayerHub(myUsername, myPlayerNumber)
                        lightUpSquare(myPlayerNumber, 1)
                        //TODO add waiting for other players message
                        myTotalPlayerNumbers[myPlayerNumber - 1] = myPlayerNumber
                    } catch (ex: Exception) {
                        //Log.w(TAG, "requestListener:onChildAdded", ex)
                        Toast.makeText(
                            applicationContext, "Failed to listen for added Request-Child.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                   // Log.w(TAG, "requestListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Requests.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // waits for accept of request to store confirmed player details
    private fun listenForAccepts() {
        myRef.child("Users").child(myUsername).child("Accepts")
            .addChildEventListener(object : ChildEventListener {
                // get latest accept
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        // get accept data and store confirmed guest
                        val acceptValue = snapshot.value as String
                        val acceptParts = splitString(acceptValue)
                        val acceptedPlayerNumber = acceptParts[1].toInt()
                        val acceptedPlayerName = acceptParts[0]
                        playerNames[acceptedPlayerNumber - 1] = acceptedPlayerName
                        Toast.makeText(
                            applicationContext,
                            "Accept from P$acceptedPlayerNumber:$acceptedPlayerName",
                            Toast.LENGTH_SHORT
                        ).show()
                        // disable the relevant editText & light up accept square
                        lightUpSquare(acceptedPlayerNumber, 1)
                        lightUpSquare(acceptedPlayerNumber, 2)
                        botPlayers[acceptedPlayerNumber - 1] = 0
                        renameBotButton(acceptedPlayerNumber)
                        disableUsernameInput(acceptedPlayerNumber)
                    } catch (ex: java.lang.Exception) {
                       // Log.w(TAG, "acceptListener:onChildAdded", ex)
                        Toast.makeText(
                            applicationContext, "Failed to listen for added Accept-Child.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                   // Log.w(TAG, "acceptListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Accepts.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // waits for completed game info to be sent, displays it and ready to play
    private fun listenForGames() {
        myRef.child("Users").child(myUsername).child("Games")
            .addChildEventListener(object : ChildEventListener {
                // get latest game
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        // get game name & fill in the lobby with player info
                        onlineGameName = snapshot.value.toString()
                        // first value is unique time stamp
                        val lobbyPlayers = splitString(onlineGameName)
                        for (playerNumber in 1 until lobbyPlayers.size) {
                            fillPlayerHub(lobbyPlayers[playerNumber], playerNumber)
                            lightUpSquare(playerNumber, 1)
                            lightUpSquare(playerNumber, 2)
                            lightUpSquare(playerNumber, 3)
                            maxPlayers = lobbyPlayers.size - 1
                            displayPlayerHubs(maxPlayers)
                        }
                        confirmAndClearHub.visibility = View.GONE
                        playHub.visibility = View.VISIBLE
                        reapplyHubConstraintsOnline()

                    } catch (ex: java.lang.Exception) {
                       // Log.w(TAG, "gameListener:onChildAdded", ex)
                        Toast.makeText(
                            applicationContext, "Failed to listen for added Games-Child.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                  //  Log.w(TAG, "gameListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Games.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // reapplies UI constraints when confirm button replaced with play button so UI doesn't jump around
    private fun reapplyHubConstraintsOnline() {
        val parentCL = findViewById<ConstraintLayout>(R.id.topLevelOnline)
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentCL)
        constraintSet.clear(R.id.playerHubs, ConstraintSet.BOTTOM)
        constraintSet.connect(
            R.id.playerHubs,
            ConstraintSet.BOTTOM,
            R.id.playHub,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(parentCL)
    }

    // online function to show players each others names in lobby
    private fun fillPlayerHub(username: String, playerNumber: Int) {
        when (playerNumber) {
            1 -> player1Username.setText(username)
            2 -> player2Username.setText(username)
            3 -> player3Username.setText(username)
            4 -> player4Username.setText(username)
           // else -> Log.d(TAG, "Error: player number incorrect")
        }
    }

    // recolours corresponding loading square in player colour
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

    // show or hide player hubs according to max number of players
    private fun displayPlayerHubs(maxPlayers: Int) {
        val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
        val player4Hub = findViewById<ConstraintLayout>(R.id.p4Hub)

        when (maxPlayers) {
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

    // creates a bot of easy, medium or hard difficulty and stores bot details
    fun genOnlineBot(view: View) {
        var playerNumber = 0
        var guestPlayer = ""

        when (view) {
            p1BotOnline -> {
                guestPlayer = player1Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 1
                botDetails(playerNumber, view, guestPlayer)
            }
            p2BotOnline -> {
                guestPlayer = player2Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 2
                botDetails(playerNumber, view, guestPlayer)
            }
            p3BotOnline -> {
                guestPlayer = player3Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 3
                botDetails(playerNumber, view, guestPlayer)
            }
            p4BotOnline -> {
                guestPlayer = player4Username.text.toString()
                if (checkIfEmpty(guestPlayer)) return
                playerNumber = 4
                botDetails(playerNumber, view, guestPlayer)
            }
        }
    }

    // stores player name, lights up appropriate square and button
    private fun botDetails(playerNumber: Int, view: View, guestPlayer: String) {
        playerNames[playerNumber - 1] = guestPlayer
        resetLoadingSquares(playerNumber)
        resetButtonColor(view)
        recolorButton(view)
        cycleBots(playerNumber)
    }

    // recolour all loading squares black
    private fun resetLoadingSquares(hubNumber: Int) {
        val black = R.color.black
        when (hubNumber) {
            0 -> {
                for (loadingSquareID in loadingSquareIDs) {
                    val loadingSquare = findViewById<View>(loadingSquareID)
                    loadingSquare.setBackgroundColor(resources.getColor(black))
                }
            }
            1 -> {
                for (loadingSquareID in 0..2) {
                    val loadingSquare = findViewById<View>(loadingSquareIDs[loadingSquareID])
                    loadingSquare.setBackgroundColor(resources.getColor(black))
                }
            }
            2 -> {
                for (loadingSquareID in 3..5) {
                    val loadingSquare = findViewById<View>(loadingSquareIDs[loadingSquareID])
                    loadingSquare.setBackgroundColor(resources.getColor(black))
                }
            }
            3 -> {
                for (loadingSquareID in 6..8) {
                    val loadingSquare = findViewById<View>(loadingSquareIDs[loadingSquareID])
                    loadingSquare.setBackgroundColor(resources.getColor(black))
                }
            }
            4 -> {
                for (loadingSquareID in 9..11) {
                    val loadingSquare = findViewById<View>(loadingSquareIDs[loadingSquareID])
                    loadingSquare.setBackgroundColor(resources.getColor(black))
                }
            }
        }
    }

    // cycle through easy, medium or hard (1-2-3) bots, fill array, and light squares
    private fun cycleBots(playerNumber: Int) {
        botPlayers[playerNumber - 1]++
        if (botPlayers[playerNumber - 1] >= 4) {
            botPlayers[playerNumber - 1] = 1
        }
        lightUpSquare(playerNumber, botPlayers[playerNumber - 1])
        //Log.d(TAG, "${botPlayers[playerNumber - 1]}")
        renameBotButton(playerNumber)
    }

    // rename corresponding bot button to display correct difficulty
    private fun renameBotButton(playerNumber: Int) {
        val botNames = arrayOf("Bot", "Easy-Bot", "Med-Bot", "Hard-Bot")
        when (playerNumber) {
            1 -> p1BotOnline.text = botNames[botPlayers[playerNumber - 1]]
            2 -> p2BotOnline.text = botNames[botPlayers[playerNumber - 1]]
            3 -> p3BotOnline.text = botNames[botPlayers[playerNumber - 1]]
            4 -> p4BotOnline.text = botNames[botPlayers[playerNumber - 1]]
        }
    }

    // takes all bot info and creates string to be passed to main activity
    private fun createBotString(botPlayers: Array<Int>): String {
        var botString = ""
        for (bots in botPlayers) {
            botString += bots.toString()
            botString += "@"
        }
        return botString
    }

    // changes button colour to the corresponding player colour
    private fun recolorButton(view: View) {
        // pick the appropriate colour
        when (view) {
            p1Request, p1BotOnline, p1Accept -> {
                val playerButton = ResourcesCompat.getDrawable(resources, R.drawable.p1button, null)
                view.background = playerButton
            }
            p2Request, p2BotOnline, p2Accept -> {
                val playerButton = ResourcesCompat.getDrawable(resources, R.drawable.p2button, null)
                view.background = playerButton
            }
            p3Request, p3BotOnline, p3Accept -> {
                val playerButton = ResourcesCompat.getDrawable(resources, R.drawable.p3button, null)
                view.background = playerButton
            }
            p4Request, p4BotOnline, p4Accept -> {
                val playerButton = ResourcesCompat.getDrawable(resources, R.drawable.p4button, null)
                view.background = playerButton
            }
        }
    }

    // recolour corresponding human & bot buttons back to purple
    private fun resetButtonColor(view: View) {
        val purpleButton = resources.getDrawable(R.drawable.roundedbutton)
        when (view) {
            p1Request, p1BotOnline, p1Accept -> {
                p1Request.background = purpleButton
                p1BotOnline.background = purpleButton
                p1Accept.background = purpleButton
            }
            p2Request, p2BotOnline, p2Accept -> {
                p2Request.background = purpleButton
                p2BotOnline.background = purpleButton
                p2Accept.background = purpleButton
            }
            p3Request, p3BotOnline, p3Accept -> {
                p3Request.background = purpleButton
                p3BotOnline.background = purpleButton
                p3Accept.background = purpleButton
            }
            p4Request, p4BotOnline, p4Accept -> {
                p4Request.background = purpleButton
                p4BotOnline.background = purpleButton
                p4Accept.background = purpleButton
            }
        }
    }
}