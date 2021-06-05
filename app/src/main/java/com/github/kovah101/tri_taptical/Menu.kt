package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu.*

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

        usernameLabel.text = myUsername
        // do I need this label???

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
        onlineLobby.putExtra("email", myEmail)
        onlineLobby.putExtra("userID", myID)
        onlineLobby.putExtra("username", myUsername)

        startActivity(onlineLobby)
    }

    // Pop up for bot difficulty & number
    // Confirm to launch bot game
    fun launchBots(view: View) {
        val botLobby = Intent(this, BotLobby::class.java)
        botLobby.putExtra("username", myUsername)

        startActivity(botLobby)
    }

    fun signOut(view: View){
        // sign out
        Firebase.auth.signOut()
        // take user back to login screen
        val login = Intent(this, Login::class.java)
        startActivity(login)
    }
}