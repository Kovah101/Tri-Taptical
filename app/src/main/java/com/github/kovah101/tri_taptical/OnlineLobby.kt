package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.online_lobby.*

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

        //val username = findViewById<TextView>(R.id.username)
        username.text = myUsername

        // if Send button clicks then disable Accept as player is host
        listOf(p1Request, p2Request, p3Request, p4Request).forEach{ button ->
            button.setOnClickListener { sentButtonClicked() }
        }

        // if Accept button clicks then disable Send as player is guest
        listOf(p1Accept, p2Accept, p3Accept, p4Accept).forEach{
            it.setOnClickListener { acceptButtonClicked() }
        }
    }

    private fun sentButtonClicked(){
        Log.d("Test", "sentButton Clicked")
        p1Accept.isEnabled = false
        p2Accept.isEnabled = false
        p3Accept.isEnabled = false
        p4Accept.isEnabled = false
    }

    private fun acceptButtonClicked(){
        Log.d("Test", "acceptButton Clicked")
        p1Request.isEnabled = false
        p2Request.isEnabled = false
        p3Request.isEnabled = false
        p4Request.isEnabled = false
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