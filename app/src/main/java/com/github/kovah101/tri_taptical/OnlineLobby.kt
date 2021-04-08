package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class OnlineLobby : AppCompatActivity() {

    private var maxPlayers = 4
    private lateinit var myEmail : String
    private lateinit var myID : String
    private lateinit var myUsername : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_lobby)

        val menuIntent = intent
        myEmail = menuIntent.getStringExtra("email")
        myID = menuIntent.getStringExtra("userID")
        myUsername = menuIntent.getStringExtra("username")

        val username = findViewById<TextView>(R.id.username)
        username.text = myUsername
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