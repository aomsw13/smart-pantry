package com.example.smartpantry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.smartpantry.PhoneActivity.Companion.BOARDCAST_KEY_ID
import com.example.smartpantry.PhoneActivity.Companion.BOARDCAST_KEY_PHONE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class MyBroadcastReceiver : BroadcastReceiver()  {

    private lateinit var myRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context, "data is removed....", Toast.LENGTH_LONG).show()
        Log.d("MyBroadcastReceiver", "enter function deleteFirebase")

        val numPhone= intent?.getStringExtra(BOARDCAST_KEY_PHONE)
        val numID= intent?.getStringExtra(BOARDCAST_KEY_ID)


        val ref = FirebaseDatabase.getInstance().getReference("Unique sender id")

        Log.d("MyBroadcastReceiver", "enter phone $numPhone")
        Log.d("MyBroadcastReceiver", "enter ID $numID")


        val cutoff: Long = Date().getTime()
        Log.d("MyBroadcastReceiver", "enter date.getTime ${Date().getTime()} ")
        Log.d("MyBroadcastReceiver", "enter cut off ${cutoff.toString()}")
        myRef = FirebaseDatabase.getInstance().getReference("Unique sender id")
        val oldItems: Query = myRef.child(numID.toString()).orderByChild("timestamp").endAt(cutoff.toDouble())
        Log.d("MyBroadcastReceiver", "enter oldItems ${oldItems.toString()}")
        oldItems.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                throw error.toException();
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("MyBroadcastReceiver", "enter current itemSnapshot")
                for (itemSnapshot in snapshot.children) {
                    Log.d("MyBroadcastReceiver", "enter current ready to del")
                    itemSnapshot.ref.removeValue()
                }
            }

        })

//        val applesQuery = ref.child(numID.toString()).orderByChild("phonenumber").equalTo(numPhone)
//        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                Log.d("MyBroadcastReceiver", "enter ready delete")
//                for (appleSnapshot in dataSnapshot.children) {
//                    Log.d("MyBroadcastReceiver", "enter ready delete")
//                    appleSnapshot.ref.removeValue()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e("Mybroadcast", "onCancelled", databaseError.toException())
//            }
//        })
    }




}