package com.github.kovah101.tri_taptical

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.online_lobby.*

class OnlineLobby : AppCompatActivity() {

    private var maxPlayers = 4
    private lateinit var myEmail: String
    private lateinit var myID: String
    private lateinit var myUsername: String
    private lateinit var hostUsername: String
    private var myPlayerNumber = 0
    private var playerNames = arrayOf("","","","")
    // instance of database
    private val database = Firebase.database
    private val myRef = database.reference
    private val TAG = "OnlineLobby"
    // loading square ids
    private val loadingSquareIDs = arrayOf(
        R.id.p1BigSquare,
        R.id.p2BigSquare,
        R.id.p3BigSquare,
        R.id.p4BigSquare
    )
//    private val loadingSquareIDs = arrayOf(
//        p1BigSquare, p1MiddleSquare, p1SmallSquare,
//        p2BigSquare, p2MiddleSquare, p2SmallSquare,
//        p3BigSquare, p3MiddleSquare, p3SmallSquare,
//        p4BigSquare, p4MiddleSquare, p4SmallSquare)

    // TODO: Make sure send and receive + listeners work

    // TODO : Confirm button - create gameName string from playerNames and send to relevant players,
    //  create game branch and launch game, adding gameName in intent to fill in player names and derive turn order using myUsername
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_lobby)

        // extract info from login page
        val menuIntent = intent
        myEmail = menuIntent.getStringExtra("email")
        myID = menuIntent.getStringExtra("userID")
        myUsername = menuIntent.getStringExtra("username")

        //set username field to your actual username
        username.text = myUsername

        // set up request listeners on your own Requests branch of database
        //listenForInvites()

        // set up accept listeners on your own Accepts branch of database
        //listenForAccepts()

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
        when (view) {
            p1Request -> {
                playerNumber = 1
                lightUpSquare(playerNumber, 1)
                guestPlayer = player1Username.text.toString()
            }
            p2Request -> {
                playerNumber = 2
                lightUpSquare(playerNumber, 1)
                guestPlayer = player2Username.text.toString()
            }
            p3Request -> {
                playerNumber = 3
                lightUpSquare(playerNumber, 1)
                guestPlayer = player3Username.text.toString()
            }
            p4Request -> {
                playerNumber = 4
                lightUpSquare(playerNumber, 1)
                guestPlayer = player4Username.text.toString()
            }
        }
        myRef.child("Users").child(guestPlayer).child("Requests").push()
            .setValue("$myUsername@$playerNumber")

    }

    // send accept invite back to host with confirmed name and lock edit texts
    // accept listener will lock in
    // lights up 2nd loading square
    fun acceptRequest(view: View) {
        when (view) {
            p1Accept -> lightUpSquare(1,2)
            p2Accept -> lightUpSquare(2,2)
            p3Accept -> lightUpSquare(3,2)
            p4Accept -> lightUpSquare(4,2)
        }
        myRef.child("Users").child(hostUsername).child("Accepts").push()
            .setValue("$myUsername@$myPlayerNumber")

    }

    // clears your Request and Accept lists
    // colours loading squares black
    // TODO reset text field and buttons to clickable = true
    fun clearInvites(view: View) {
        myRef.child("Users").child(myUsername).child("Requests").setValue(myEmail)
        myRef.child("Users").child(myUsername).child("Accepts").setValue(myEmail)

        val black = R.color.black
        for (loadingSquareID in loadingSquareIDs){
            val loadingSquare = findViewById<View>(loadingSquareID)
            loadingSquare.setBackgroundColor(resources.getColor(black))
        }
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
                        Toast.makeText(
                            applicationContext,
                            "Request from $hostUsername, You are player:${requestParts[1]}",
                            Toast.LENGTH_SHORT
                        ).show()
                        fillPlayerHub(myUsername, myPlayerNumber)
                        //TODO add waiting for other players and possibility of being multiple players
                    } catch (ex: Exception) {
                        Log.w(TAG, "requestListener:onChildAdded", ex)
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
                    Log.w(TAG, "requestListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Requests.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun listenForAccepts(){
        myRef.child("Users").child(myUsername).child("Accepts")
            .addChildEventListener(object  : ChildEventListener{
                // get latest accept
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        // get accept data and store confirmed guest
                        val acceptValue = snapshot.value as String
                        val acceptParts = splitString(acceptValue)
                        val acceptedPlayerNumber = acceptParts[1].toInt()
                        val acceptedPlayerName = acceptParts[0]
                        playerNames[acceptedPlayerNumber-1] = acceptedPlayerName
                        Toast.makeText(
                            applicationContext,
                            "Accept from $acceptedPlayerName",
                            Toast.LENGTH_SHORT).show()
                        // disable the relevant editText & light up accept square
                        lightUpSquare(acceptedPlayerNumber, 2)
                        disableUsernameInput(acceptedPlayerNumber)
                    } catch (ex: java.lang.Exception){
                        Log.w(TAG, "acceptListener:onChildAdded", ex)
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
                    Log.w(TAG, "acceptListener:onCancelled", error.toException())
                    Toast.makeText(
                        applicationContext, "Failed to listen to Accepts.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun fillPlayerHub(username: String, playerNumber: Int) {
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
                1 -> p1BigSquare.setBackgroundColor(playerColors[playerNumber - 1])
                2 -> p1MiddleSquare.setBackgroundColor(playerColors[playerNumber - 1])
                3 -> p1SmallSquare.setBackgroundColor(playerColors[playerNumber - 1])
            }
            2 -> when (stageToColor) {
                1 -> p2BigSquare.setBackgroundColor(playerColors[playerNumber - 1])
                2 -> p2MiddleSquare.setBackgroundColor(playerColors[playerNumber - 1])
                3 -> p2SmallSquare.setBackgroundColor(playerColors[playerNumber - 1])
            }
            3 -> when (stageToColor) {
                1 -> p3BigSquare.setBackgroundColor(playerColors[playerNumber - 1])
                2 -> p3MiddleSquare.setBackgroundColor(playerColors[playerNumber - 1])
                3 -> p3SmallSquare.setBackgroundColor(playerColors[playerNumber - 1])
            }
            4 -> when (stageToColor) {
                1 -> p4BigSquare.setBackgroundColor(playerColors[playerNumber - 1])
                2 -> p4MiddleSquare.setBackgroundColor(playerColors[playerNumber - 1])
                3 -> p4SmallSquare.setBackgroundColor(playerColors[playerNumber - 1])
            }
        }
    }

    // disables username input field in UI
    private fun disableUsernameInput(playerNumber: Int){
        // 0 is not editable
        when(playerNumber){
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
        if (maxPlayers >= 3) {
            val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
            player3Hub.visibility = View.VISIBLE
        }
        if (maxPlayers == 4) {
            val player4Hub = findViewById<ConstraintLayout>(R.id.p4Hub)
            player4Hub.visibility = View.VISIBLE
        }
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
        if (maxPlayers < 4) {
            val player4Hub = findViewById<ConstraintLayout>(R.id.p4Hub)
            player4Hub.visibility = View.GONE
        }
        if (maxPlayers < 3) {
            val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
            player3Hub.visibility = View.GONE
        }
        val playerNumberView = findViewById<TextView>(R.id.maxPlayers)
        playerNumberView.text = maxPlayers.toString()
    }
}