package com.example.smartpantry

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class PhoneActivity : AppCompatActivity() {

    private lateinit var mauth: FirebaseAuth
    private lateinit var signout_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
//        setSupportActionBar(findViewById(R.id.toolbar))

        signout_button = findViewById(R.id.signout_button)
        mauth = FirebaseAuth.getInstance()

        val textView = findViewById<TextView>(R.id.message_success)


        signout_button.setOnClickListener { view: View? ->
            mauth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "logout success", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(mauth.currentUser == null){
            startActivity(Intent(this, MainActivity::class.java))
        }
        else{
            Toast.makeText(this, "already sign in", Toast.LENGTH_SHORT)
                .show()
        }
    }
}