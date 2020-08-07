package com.example.smartpantry

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        // a user when user click taker button and navigate a user to MainActivity
        takerButton.setOnClickListener { view: View? ->
            val mIntent = Intent(this, MainActivity::class.java)
            //mIntent.putExtra("userID", "1")
            startActivity(mIntent)
        }

        // a user when user click giver button and navigate a user to MainActivity
        giverButton.setOnClickListener { view: View? ->
            val mIntent = Intent(this, MainActivityGiver::class.java)
            //mIntent.putExtra("userID", "0")
            startActivity(mIntent)
            // startMqtt()
        }
    }

//    private fun startMqtt() {
//        mqttGiver.connect(this.applicationContext)
//        mqttGiver.subscriptionTopicPantryId = "pantry/+/emptyPantryId"
//        mqttGiver.subscriptionTopicPantryStatus = "pantry/+/emptyPantryStatus"
//        mqttGiver.publishTopic = "pantry/+/statusGiver"
//        mqttGiver.publishTextMessage = "open"
//        Log.d("FETCHING", "startMqtt")
//        //setValueToFirebase(mqttGiver.receiveTopicPantryId.toString(), mqttGiver.receiveTopicPantryStatus)
//    }


    // two below functions, OncreateOptionsMenu & onOptionsItemSelected, will not use in android mobile application
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}