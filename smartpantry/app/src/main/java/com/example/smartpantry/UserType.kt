package com.example.smartpantry

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserType : AppCompatActivity() {

    private lateinit var takerButton: Button
    private lateinit var giverButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usertype)


        takerButton = findViewById(R.id.taker_button)
        giverButton = findViewById(R.id.giver_button)

        takerButton.setOnClickListener { view: View? ->
            val mIntent = Intent(this, MainActivity::class.java)
           // mIntent.putExtra("userID", "1")
            startActivity(mIntent)
        }

        giverButton.setOnClickListener { view: View? ->
            val mIntent = Intent(this, MainActivityGiver::class.java)
//            mIntent.putExtra("userID", "0")
            startActivity(mIntent)

        }
    }
}