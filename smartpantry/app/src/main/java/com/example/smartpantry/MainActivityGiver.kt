package com.example.smartpantry

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.smartpantry.Adapter.MyAdapter
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import helpers.MqttGiver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivityGiver : AppCompatActivity() {

    private lateinit var phonenumber: EditText
    private lateinit var codeID: EditText
    private lateinit var send_button: Button
    private lateinit var verify_button: Button
    var verificationCode: String = ""
    var input_phonenumber:String = ""

    //firebase
    private lateinit var myRef: DatabaseReference // = FirebaseDatabase.getInstance().getReference()  //point to the root named "penquiz3d349"
    private lateinit var auth: FirebaseAuth
    var id: String = ""

    //phoen number
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    /*--positiveButtonClick -> pass the Button text along with a Kotlin function that’s triggered when that button is clicked.
    The function is a part of the DialogInterface.OnClickListener() interface
    DialogInterface is an instance of the Dialog and Int is the id of the Button that is clicked.*/
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(this, android.R.string.yes, Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maingiver)

        auth = FirebaseAuth.getInstance()
        phonenumber = findViewById(R.id.phone_id_giver)
        codeID = findViewById(R.id.verification_id_giver)
        send_button = findViewById(R.id.send_button_giver)
        verify_button = findViewById(R.id.verify_button_giver)

        // title name on of each interface
        supportActionBar!!.title = "Giver"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // button to get verification code
        send_button.setOnClickListener { view: View? ->
            verify()
        }

        // button to sign in after get verification code
        verify_button.setOnClickListener { view: View? ->
            authenticate()
            //startMqtt()
        }
    }


//    private fun startMqtt() {
//        mqttGiver.connect(this.applicationContext)
//        mqttGiver.subscriptionTopicPantryId = "pantry/+/emptyPantryId"
//        mqttGiver.subscriptionTopicPantryStatus = "pantry/+/emptyPantryStatus"
//        mqttGiver.publishTextMessage = "open"
//        Log.d("FETCHING", "startMqtt")
//        //setValueToFirebase(mqttGiver.receiveTopicPantryId.toString(), mqttGiver.receiveTopicPantryStatus)
//    }


    // navigate user to next interface after user successfully sign in
    private fun signin(credential: PhoneAuthCredential) {

        val currentTimestamp = Date().getTime()
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        Log.d("MainActivityGiver", "currentTimeStamp $currentTimestamp")
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        id = auth.currentUser?.uid.toString()


        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){

               // Toast.makeText(this@MainActivityGiver, "login sucessful", Toast.LENGTH_SHORT).show()

                val userButtonId= intent?.getStringExtra("userID")
                Log.d("MainActivityGiver", "user type id $userButtonId")
                Log.d("MainActivityGiver", "enter giver type")

                // to start new activity when user successfully signin
                //send ID of user's phone number to PantryActivity to keep pantry id when user select
                val mIntent = Intent(this, PantryActivity::class.java)
                //mIntent.putExtra("pantryKeyID", id)
                startActivity(mIntent)

                // store information of pantry that takers request to firebase
                myRef = FirebaseDatabase.getInstance().getReference("Unique giver id").child(auth.currentUser?.uid.toString())
                myRef.child("phonenumber").setValue(input_phonenumber)
                myRef.child("date & time").setValue(formatted)
                myRef.child("timestamp").setValue(currentTimestamp)

            }
            else{
                //Toast.makeText(this@MainActivityGiver, "cannot login", Toast.LENGTH_SHORT).show()
                // display pop up window when user cannot sign in to the system
                val title:String =  "Error"
                val msg:String = "Verification code is incorrect"
                displayDialog(title, msg)
            }
        }

    }

    // call back function that handle the result of the request
    private fun verificationcallback(){

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //Toast.makeText(this@MainActivityGiver, "verification completed!!!!!!!"+ credential, Toast.LENGTH_SHORT).show()
                //signin(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                //Toast.makeText(this@MainActivityGiver, "verification fialed" + e, Toast.LENGTH_SHORT).show()
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.w("MainActivityGiver", "onVerificationFailed", e)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.w("MainActivityGiver", "onVerificationFailed", e)
                }
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("MainActivityGiver", "onCodesent "+s)
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s.toString()
                Log.d("MainActivityGiver", "verificationCode $verificationCode")
                //Toast.makeText(this@MainActivityGiver, "Code sent", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // to check phone number callback
    private fun verify() {
        input_phonenumber = phonenumber.text.toString()
        verificationcallback()
        sendverificationNumber(input_phonenumber)
        val title:String =  "Notification"
        val msg:String = "verification code has been sent to your mobile phone "
        displayDialog(title, msg)
    }

    // send verification code to the user's phone
    private fun sendverificationNumber(inputPhonenumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            inputPhonenumber,                     // Phone number to verify
            120,                           // Timeout duration
            TimeUnit.SECONDS,                // Unit of timeout
            this,        // Activity (for callback binding)
            callbacks)
    }

    // to check that user input verification code correctly or not
    private fun authenticate() {
        val input_code:String = codeID.getText().toString()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, input_code)
        signin(credential)
    }

    // to display dialog once user click a button
    private fun displayDialog(title: String, msg: String) {
        var builder = AlertDialog.Builder(this)
        // Set the alert dialog title
        builder.setTitle(title)
        // Display a message on alert dialog
        builder.setMessage(msg)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.show()
        //   Toast.makeText(this@MainActivity, "invalid verification", Toast.LENGTH_SHORT).show()
    }
}