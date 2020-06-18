package com.example.smartpantry

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(){

    private lateinit var pantryID: EditText
    private lateinit var phonenumber: EditText
    private lateinit var codeID: EditText
    private lateinit var send_button:Button
    private lateinit var verify_button:Button
    var verificationCode: String = ""
    var input_pantryID: String = ""
    var input_phonenumber:String = ""

    //firebase
    private lateinit var myRef: DatabaseReference // = FirebaseDatabase.getInstance().getReference()  //point to the root named "penquiz3d349"
    private lateinit var auth: FirebaseAuth


    //phoen number
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var check: Boolean = true


    /*--positiveButtonClick -> pass the Button text along with a Kotlin function that’s triggered when that button is clicked.
    The function is a part of the DialogInterface.OnClickListener() interface
    DialogInterface is an instance of the Dialog and Int is the id of the Button that is clicked.*/
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(this,
            android.R.string.yes, Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        auth = FirebaseAuth.getInstance()
        pantryID = findViewById(R.id.pantry_id)
        phonenumber = findViewById(R.id.phone_id)
        codeID = findViewById(R.id.verification_id)
        send_button = findViewById(R.id.send_button)
        verify_button = findViewById(R.id.verify_button)


        //mDatabase = FirebaseDatabase.getInstance().getReference("antry ID")

        send_button.setOnClickListener { view: View? ->
            verify()}

        verify_button.setOnClickListener { view: View? ->
            authenticate()

        }

    }

    private fun verificationcallback(){

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(this@MainActivity, "verification completed!!!!!!!"+ credential, Toast.LENGTH_SHORT)
                    .show()
                //signin(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, "verification fialed" + e, Toast.LENGTH_SHORT).show()
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.w("MainActivity", "onVerificationFailed", e)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.w("MainActivity", "onVerificationFailed", e)
                }
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken
            ) {
                Log.d("MainActivity", "onCodesent "+s)
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s.toString()
                Toast.makeText(this@MainActivity, "Code sent", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun signin(credential: PhoneAuthCredential) {

        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        input_pantryID= pantryID.getText().toString()

            auth.signInWithCredential(credential).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this@MainActivity, "login sucessful", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, PhoneActivity::class.java))

                    val id =auth.currentUser?.uid
                    myRef = FirebaseDatabase.getInstance().getReference("Unique sender id").child(id.toString())

                    myRef.child("phonenumber").setValue(input_phonenumber)
                    myRef.child("success").setValue(true)
                    myRef.child("date & time").setValue(formatted)
                    myRef.child("pantry id").setValue(input_pantryID)

//                    myRef = FirebaseDatabase.getInstance().getReference("phone number")
//                    myRef.child(input_phonenumber).setValue(input_phonenumber)


                }
                else{
                    Toast.makeText(this@MainActivity, "cannot login", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }


    private fun verify() {

        input_phonenumber = phonenumber.text.toString()
        checkPhoneNumbercallback(input_phonenumber)

    }

    private fun checkPhoneNumbercallback(inputPhonenumber: String) {
        myRef = FirebaseDatabase.getInstance().getReference("Unique sender id")
        myRef.keepSynced(true)
        myRef.orderByChild("phonenumber").equalTo(input_phonenumber.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    check = false
                    Toast.makeText(this@MainActivity, "exist", Toast.LENGTH_SHORT)
                        .show()

                }
                else{
                    check = true
                    verificationcallback()
                    sendverificationNumber(inputPhonenumber)

                    Toast.makeText(this@MainActivity, "not exist", Toast.LENGTH_SHORT)
                        .show()
//                    startActivity(Intent(this@MainActivity,VerificationActivity::class.java))
                }
            }


        })
    }


    private fun sendverificationNumber(inputPhonenumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            inputPhonenumber,                     // Phone number to verify
            120,                           // Timeout duration
            TimeUnit.SECONDS,                // Unit of timeout
            this,        // Activity (for callback binding)
            callbacks)

    }

    private fun authenticate() {

        Toast.makeText(this@MainActivity, "checl code"+verificationCode, Toast.LENGTH_SHORT)
            .show()
        //phone number already exist
        if(check == true){
            val input_code:String = codeID.getText().toString()
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, input_code)
            signin(credential)
            Toast.makeText(this@MainActivity, "verify" + credential, Toast.LENGTH_SHORT).show()
        }
        //phone number does not exist and verification vode is sent
        else if(check == false){
//            var builder = AlertDialog.Builder(this)
//            // Set the alert dialog title
//            builder.setTitle("THANK YOU")
//            // Display a message on alert dialog
//            builder.setMessage("Your message has been sent")
//            builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
//            // Finally, make the alert dialog using builder
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
            Toast.makeText(this@MainActivity, "invalid verification", Toast.LENGTH_SHORT)
                .show()
        }

    }


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









