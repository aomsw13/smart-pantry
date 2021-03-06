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


// this class will display giver's information and selected pantry after giver select pantry id from recyclerview

class GiverSelectPantryActivity : AppCompatActivity() {

    private lateinit var pantryID: TextView  //Textview is not EditText ()
    private lateinit var signoutButton: Button

    //firebase
    private lateinit var mauth: FirebaseAuth
    private lateinit var myRef: DatabaseReference
    var id: String = ""

    // TAG
    var TAG = "GiverSelectPantryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giverselectpantry)

        pantryID = findViewById(R.id.pantry_title_id)
        signoutButton = findViewById(R.id.signout_button)
        mauth = FirebaseAuth.getInstance()
        id = mauth.currentUser?.uid.toString()

        // title name on of each interface
        supportActionBar!!.title = "start giving"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //call getIncomingIntent function
        getIncomingIntent()

        // button that allow giver to click sign out after giver successfully sign in
        signoutButton.setOnClickListener { view: View? ->
            startActivity(Intent(this, UserType::class.java))
            mauth.signOut()
        }
    }

    // function aims to get selected pantry id from giver who select id from recyclerview
    private fun getIncomingIntent(){
        Log.d(TAG, "get incomingIntent : cheking for incoming intent")
        if(getIntent().hasExtra("pantryIdAdapter")){
            val mPantryId = intent.getStringExtra ("pantryIdAdapter")
            Log.d(TAG, "get incomingIntent : found incoming intent "+ mPantryId)
            setPantryIdDescription(mPantryId.toString())
        }
    }

    //set and display selected pantry id
    private fun setPantryIdDescription(pantryNumber: String){
        Log.d(TAG, "setting pantry id description")
        pantryID.setText(pantryNumber)
        storeIdToFirebase(pantryNumber)
    }

    //store giver's selected pantry in firebases
    private fun storeIdToFirebase(pantryNumber: String) {
        Log.d(TAG, "mauthID "+ id)
        myRef = FirebaseDatabase.getInstance().getReference("Unique giver id").child(id)
        myRef.child("pantry id").setValue(pantryNumber)
    }


}