package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

class Menu : AppCompatActivity() {

    private lateinit var myEmail : String
    private lateinit var myID : String
    private lateinit var myUsername : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val loginIntent = intent
         myEmail = loginIntent.getStringExtra("email")
         myID = loginIntent.getStringExtra("userID")
         myUsername = loginIntent.getStringExtra("username")
    }

    private val menuTag = "Menu"

    // starts classic local multi-player game
    fun launchLocalGame(view: View) {
        val localGame = Intent(this, MainActivity::class.java)
        startActivity(localGame)
    }

    // Goes to match making screen
    // players can host or join games
    fun launchOnline(view: View) {
        val onlineLobby = Intent(this, OnlineLobby::class.java)
        onlineLobby.putExtra("email", myEmail)
        onlineLobby.putExtra("userID", myID)
        onlineLobby.putExtra("username", myUsername)

        startActivity(onlineLobby)

        //Log.d(menuTag, "Online Games coming soon")
        //Toast.makeText(applicationContext, "$myEmail, $myID, $myUsername", Toast.LENGTH_SHORT).show()
    }

    // Pop up for bot difficulty & number
    // Confirm to launch bot game
    fun launchBots(view: View) {
        Log.d(menuTag, "Bot Game coming soon")
        Toast.makeText(applicationContext, "Bot Games Coming Soon", Toast.LENGTH_SHORT).show()

    }
}