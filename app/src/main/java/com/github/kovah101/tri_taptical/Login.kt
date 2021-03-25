package com.github.kovah101.tri_taptical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
    }


    private fun loginButtonClick(view: View) {
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
        } else{
            // No user so create new one
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_SHORT)
                            .show()
                        // set user name
                        val currentUser = mAuth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }
                        currentUser!!.updateProfile(profileUpdates)
                            .addOnCompleteListener{
                                if (it.isSuccessful){
                                    Toast.makeText(applicationContext, "Successfully updated display name", Toast.LENGTH_SHORT)

                                }
                            }
                    } else {
                        Toast.makeText(applicationContext, "Failed login", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
        }
    }
}