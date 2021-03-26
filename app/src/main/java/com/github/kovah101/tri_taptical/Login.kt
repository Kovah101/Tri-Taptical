package com.github.kovah101.tri_taptical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        loadMenu()

    }

    fun loadMenu(){
        // Check if user is signed in (non-null) and move to menu
        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            Toast.makeText(applicationContext, "Already logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Menu::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("userID", currentUser.uid)
            intent.putExtra("userName", currentUser.displayName)

            startActivity(intent)
        }
    }

     fun loginButtonClick(view: View) {
        // take info and login to firebase
        loginToFireBase(
            userEmail.text.toString(),
            userPassword.text.toString(),
            userName.text.toString()
        )
    }

    private fun loginToFireBase(email: String, password: String, name: String) {
        // check if already logged in
        val user = mAuth.currentUser
        if (user != null) {
            // user already signed in
            //TODO skip to menu screen
            Toast.makeText(applicationContext, "Already logged in", Toast.LENGTH_SHORT).show()
            loadMenu()

        } else {
            // No user so create new one
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_SHORT)
                            .show()
                        // set user name
                        val currentUser = mAuth.currentUser
                        // compiler error
//                        val profileUpdates = userProfileChangeRequest {
//                            displayName = name
//                        }
                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                        currentUser!!.updateProfile(profileUpdates)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Successfully updated display name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        loadMenu()
                    } else {
                        Toast.makeText(applicationContext, "Failed login", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("Task", "${task.exception}")
                    }
                }
        }
    }
}