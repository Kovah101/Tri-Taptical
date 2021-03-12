package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun changeColor(view: View) {
            val selectedButton = view as Button
            var cellID = ""
        when(selectedButton.id){
            R.id.topLeft -> cellID = "topLeft"
            R.id.topMiddle -> cellID = "topMiddle"
            R.id.topRight -> cellID = "topRight"
        }
        Log.d("ButtonPress","selected button: ${selectedButton.id}")
    }

    fun confirmMove(view: View) {

    }
}