package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
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
        Log.d(menuTag, "Online Games coming soon")
        Toast.makeText(applicationContext, "Online Games Coming Soon", Toast.LENGTH_SHORT).show()
    }

    // Pop up for bot difficulty & number
    // Confirm to launch bot game
    fun launchBots(view: View) {
        Log.d(menuTag, "Bot Game coming soon")
        Toast.makeText(applicationContext, "Bot Games Coming Soon", Toast.LENGTH_SHORT).show()

    }
}