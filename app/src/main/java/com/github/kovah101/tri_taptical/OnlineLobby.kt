package com.github.kovah101.tri_taptical

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var myEmail : String
    private lateinit var myID : String
    private lateinit var myUsername : String
    private var myPlayerNumber = 0
    // instance of database
    private val database = Firebase.database
    private val myRef = database.getReference()
    private val TAG = "OnlineLobby"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_lobby)

        val menuIntent = intent
        myEmail = menuIntent.getStringExtra("email")
        myID = menuIntent.getStringExtra("userID")
        myUsername = menuIntent.getStringExtra("username")

        //set username field to your actual username
        username.text = myUsername

        // set up request listeners on your own branch of database
        listenForInvites()

        // if Send button clicks then disable Accept as player is host
        listOf(p1Request, p2Request, p3Request, p4Request).forEach{ button ->
            button.setOnClickListener { sentButtonClicked() }
        }

        // if Accept button clicks then disable Send as player is guest
        listOf(p1Accept, p2Accept, p3Accept, p4Accept).forEach{
            it.setOnClickListener { acceptButtonClicked() }
        }
    }

    // send play request with hosts name and guest player number
    fun sendRequest (view: View){
        var playerNumber = 0
        var guestPlayer = ""
        when (view){
            p1Request -> {
                p1BigSquare.setBackgroundColor(resources.getColor(R.color.playerOne))
                playerNumber = 1
                guestPlayer = player1Username.text.toString()
            }
            p2Request -> {
                p2BigSquare.setBackgroundColor(resources.getColor(R.color.playerTwo))
                playerNumber = 2
                guestPlayer = player2Username.text.toString()
            }
            p3Request -> {
                p3BigSquare.setBackgroundColor(resources.getColor(R.color.playerThree))
                playerNumber = 3
                guestPlayer = player3Username.text.toString()
            }
            p4Request -> {
                p4BigSquare.setBackgroundColor(resources.getColor(R.color.playerFour))
                playerNumber = 4
                guestPlayer = player4Username.text.toString()
            }
        }
        myRef.child("Users").child(guestPlayer).child("Requests").push().setValue(myUsername+"@"+playerNumber)

    }

    fun acceptRequest (view: View){

    }
    // listen to your own requests in database
    private fun listenForInvites(){
        myRef.child("Users").child(myUsername).child("Requests")
            .addChildEventListener(object : ChildEventListener {
                // get latest request
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val requestValue = snapshot.value as String
                        val requestParts = splitString(requestValue)
                        myPlayerNumber = requestParts[1].toInt()
                        Log.d(TAG, "Host:${requestParts[0]}")
                        Log.d(TAG, "I am Player:$myPlayerNumber")
                        //TODO Fill in your own username to correct Hub then implement Accept button
                    }catch (ex: Exception){
                        Log.w(TAG, "requestListener:onChildAdded", ex)
                        Toast.makeText(applicationContext, "Failed to listen for added Child.",
                            Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(applicationContext, "Failed to listen to Requests.",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    // splits string into list around "@"
    private fun splitString(string : String): List<String> {
        return string.split("@")
    }

    private fun sentButtonClicked(){
        Log.d("Test", "sentButton Clicked")
        p1Accept.isClickable = false
        p2Accept.isClickable = false
        p3Accept.isClickable = false
        p4Accept.isClickable = false
    }

    private fun acceptButtonClicked(){
        Log.d("Test", "acceptButton Clicked")
        p1Request.isClickable = false
        p2Request.isClickable = false
        p3Request.isClickable = false
        p4Request.isClickable = false
    }

    // increase max player count
    fun incrementPlayers(view: View) {
        Log.d("Test", "Button Clicked successfully")
        maxPlayers ++
        if (maxPlayers > 4) {
            maxPlayers = 4
        }
        if (maxPlayers >= 3){
            val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
            player3Hub.visibility = View.VISIBLE
        }
        if (maxPlayers == 4){
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
        if (maxPlayers < 4){
            val player4Hub = findViewById<ConstraintLayout>(R.id.p4Hub)
            player4Hub.visibility = View.GONE
        }
        if (maxPlayers < 3){
            val player3Hub = findViewById<ConstraintLayout>(R.id.p3Hub)
            player3Hub.visibility = View.GONE
        }
        val playerNumberView = findViewById<TextView>(R.id.maxPlayers)
        playerNumberView.text = maxPlayers.toString()
    }
}