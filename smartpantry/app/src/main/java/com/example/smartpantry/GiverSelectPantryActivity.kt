package com.example.smartpantry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GiverSelectPantryActivity : AppCompatActivity() {

    private lateinit var pantryID: TextView  //Textview is not EditText ()
    private lateinit var signoutButton: Button

    //firebase
    private lateinit var mauth: FirebaseAuth
    private lateinit var myRef: DatabaseReference
    var id: String = ""

    var TAG = "GiverSelectPantryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giverselectpantry)

        pantryID = findViewById(R.id.pantry_title_id)
        signoutButton = findViewById(R.id.signout_button)
        mauth = FirebaseAuth.getInstance()
        id = mauth.currentUser?.uid.toString()

        supportActionBar!!.title = "start giving"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG, "already changed page")
        getIncomingIntent()

        signoutButton.setOnClickListener { view: View? ->

            startActivity(Intent(this, UserType::class.java))
            mauth.signOut()

        }
    }

    private fun getIncomingIntent(){
        Log.d(TAG, "get incomingIntent : cheking for incoming intent")
        if(getIntent().hasExtra("pantryIdAdapter")){
            val mPantryId = intent.getStringExtra ("pantryIdAdapter")
            Log.d(TAG, "get incomingIntent : found incoming intent "+ mPantryId)
            setPantryIdDescription(mPantryId.toString())
        }
    }

    private fun setPantryIdDescription(pantryNumber: String){
        Log.d(TAG, "setting pantry id description")
        pantryID.setText(pantryNumber)
        storeIdToFirebase(pantryNumber)
    }

    private fun storeIdToFirebase(pantryNumber: String) {
        Log.d(TAG, "mauthID "+ id)
        myRef = FirebaseDatabase.getInstance().getReference("Unique giver id").child(id)
        myRef.child("pantry id").setValue(pantryNumber)
    }


}