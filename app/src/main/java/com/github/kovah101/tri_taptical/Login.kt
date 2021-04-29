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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    // instance of database
    private val database = Firebase.database
    private val myRef = database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        loadMenu()
    }

    // TODO login and sign up buttons for new or old users

    private fun loadMenu() {
        // Check if user is signed in (non-null) and move to menu
        val currentUser = mAuth.currentUser
        if (currentUser != null) {

            val intent = Intent(this, Menu::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("userID", currentUser.uid)
            intent.putExtra("username", currentUser.displayName)

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
        loadMenu()
    }

    fun signUp(view: View) {
        // take info and sign up to firebase
        signUpToFireBase(
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
            Toast.makeText(applicationContext, "Already logged in", Toast.LENGTH_SHORT).show()
            loadMenu()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Failed login - ${task.exception}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.d("Task", "${task.exception}")
                    }
                }
        }
    }


    private fun signUpToFireBase(email: String, password: String, name: String) {
        // No user so create new one
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Successful create user", Toast.LENGTH_SHORT)
                        .show()
                    // sign in
                    mAuth.signInWithEmailAndPassword(email, password)
                    // set user name
                    val currentUser = mAuth.currentUser
                    val profileUpdates =
                        UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    currentUser!!.updateProfile(profileUpdates)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Successfully updated display name",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // save to database then create Requests, Accepts & Games branches
                                myRef.child("Users").child(currentUser.displayName!!)
                                    .setValue(currentUser.uid)
                                myRef.child("Users").child(currentUser.displayName!!)
                                    .child("Requests")
                                    .setValue(currentUser.email)
                                myRef.child("Users").child(currentUser.displayName!!)
                                    .child("Accepts")
                                    .setValue(currentUser.email)
                                myRef.child("Users").child(currentUser.displayName!!).child("Games")
                                    .setValue(currentUser.email)
                            }
                        }
                    loadMenu()
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        "Failed sign up - ${task.exception}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d("Task", "${task.exception}")

                }
            }
    }
}
