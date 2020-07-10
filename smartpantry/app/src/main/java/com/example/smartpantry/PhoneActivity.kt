package com.example.smartpantry

import android.R.attr.key
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


open class PhoneActivity : MainActivity() {

    private lateinit var mauth: FirebaseAuth
    private lateinit var signout_button: Button


    companion object{
        val BOARDCAST_KEY_ID: String = "keyIDBroadcast"
        val BOARDCAST_KEY_PHONE: String = "keyNoBroadcast"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
//        setSupportActionBar(findViewById(R.id.toolbar))

        signout_button = findViewById(R.id.signout_button)
        mauth = FirebaseAuth.getInstance()

        signout_button.setOnClickListener { view: View? ->

            startAlert()
            startActivity(Intent(this, UserType::class.java))
          //  mauth.signOut()
//            startActivity(Intent(this, ReadyToDelete::class.java))
//            Toast.makeText(this, "logout success", Toast.LENGTH_SHORT)
//                .show()


        }


    }


//    override fun onStart() {
//        super.onStart()
//        if(mauth.currentUser == null){
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//        else{
//            Toast.makeText(this, "already sign in", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

    fun startAlert() {
        Log.d("PhoneActivity", "enter startAlert")
        val numPhone= intent?.getStringExtra("keyNo")
        val numID= intent?.getStringExtra("keyID")

        Log.d("PhoneActivity", "enter phone $numPhone")
        Log.d("PhoneActivity", "enter ID $numID")

        val intent = Intent(this, MyBroadcastReceiver::class.java).apply {
            putExtra(BOARDCAST_KEY_PHONE,numPhone)
            putExtra(BOARDCAST_KEY_ID,numID)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this.applicationContext, 234324243, intent, 0
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 5 * 1000] = pendingIntent
        Toast.makeText(this, "Alarm set in 30 seconds", Toast.LENGTH_LONG).show()
    }



}