package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu.*

class Menu : AppCompatActivity() {

    private lateinit var myEmail : String
    private lateinit var myID : String
    private lateinit var myUsername : String
    private lateinit var mAuth: FirebaseAuth

    // instance of database
    private val database = Firebase.database
    private val myRef = database.reference
    private val TAG = "NOTIFICATION-TEST"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val loginIntent = intent
        // check if intent is from login page or notification
        if(loginIntent.getStringExtra("email") != null) {
            myEmail = loginIntent.getStringExtra("email")
            myID = loginIntent.getStringExtra("userID")
            myUsername = loginIntent.getStringExtra("username")

            usernameLabel.text = myUsername
            // do I need this label???
        }

        // listen for invites
        notifyOnInvite()
        Toast.makeText(applicationContext, "email:$myEmail, username:$myUsername", Toast.LENGTH_SHORT).show()
    }

    private val menuTag = "Menu"

    // starts classic local multi-player game
    fun launchLocalGame(view: View) {
        val localGame = Intent(this, BotLobby::class.java)
        localGame.putExtra("username", myUsername)
        startActivity(localGame)
    }

    // Goes to match making screen
    // players can host or join games
    fun launchOnline(view: View) {
        val onlineLobby = Intent(this, OnlineLobby::class.java)
        onlineLobby.putExtra("Notification", false)
        onlineLobby.putExtra("email", myEmail)
        onlineLobby.putExtra("userID", myID)
        onlineLobby.putExtra("username", myUsername)

        startActivity(onlineLobby)
    }

    fun signOut(view: View){
        // sign out
        Firebase.auth.signOut()
        // take user back to login screen
        val login = Intent(this, Login::class.java)
        startActivity(login)
    }

    // listen to your own requests in database
    private fun notifyOnInvite() {
        myRef.child("Users").child(myUsername).child("Requests")
            .addChildEventListener(object : ChildEventListener {
                // get latest request & create notification
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val requestValue = snapshot.value as String
                        val requestParts = splitString(requestValue)
                        val myPlayerNumber = requestParts[1].toInt()
                        val hostUsername = requestParts[0]
                        val notifyMe = Notifications()
                        notifyMe.createChannel(applicationContext)
                        if (hostUsername != myUsername) {
                            notifyMe.Notify(applicationContext, hostUsername, 37, myPlayerNumber, myUsername, myEmail)
                           // Log.d(TAG, "Notification created!")
                        }

                    } catch (ex: Exception) {
                       // Log.w(TAG, "requestListener:onChildAdded", ex)
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
                }
            }) }

    // splits string into list around "@"
    private fun splitString(string: String): List<String> {
        return string.split("@")
    }
}