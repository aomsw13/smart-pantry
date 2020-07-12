package com.example.smartpantry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import helpers.MqttGiver

class UserType : AppCompatActivity() {

    private lateinit var takerButton: Button
    private lateinit var giverButton: Button

    val mqttGiver: MqttGiver by lazy {
        MqttGiver(this)

    }

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
            startMqtt()


        }
    }

    private fun startMqtt() {
        mqttGiver.connect(this.applicationContext)
        mqttGiver.subscriptionTopicPantryId = "pantry/+/emptyPantryId"
        mqttGiver.subscriptionTopicPantryStatus = "pantry/+/emptyPantryStatus"
        mqttGiver.publishTextMessage = "open"
        Log.d("FETCHING", "startMqtt")
        //setValueToFirebase(mqttGiver.receiveTopicPantryId.toString(), mqttGiver.receiveTopicPantryStatus)
    }


}