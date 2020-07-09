package com.example.smartpantry

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartpantry.Adapter.MyAdapter
import com.example.smartpantry.Model.PantryEmpty
import com.example.smartpantry.callback.PantryCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.android.gms.common.api.ResultCallback as ResultCallback


class PantryActivity : AppCompatActivity(){

    private lateinit var mRecycleView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var resultRef: DatabaseReference
    private lateinit var mQueryCurrent: Query
   // private lateinit var options : FirebaseRecyclerOptions<PantryEmpty>
    private lateinit var dataList : MutableList<PantryEmpty> // To keep Quizes object retrieve from database
    private lateinit var pantryAdapter: MyAdapter
    private lateinit var user: FirebaseUser
    private lateinit var textId: TextView
    private lateinit var textStatus: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantryinfo)

        user = FirebaseAuth.getInstance().currentUser!!
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Empty Pantry")
//        mDatabase.keepSynced(true)
//        mQueryCurrent = mDatabase.orderByChild("pantryId")

        dataList = ArrayList()
        pantryAdapter = MyAdapter(dataList)

        mRecycleView = findViewById(R.id.recycleView)
        mRecycleView.layoutManager = LinearLayoutManager(this)

        textId = findViewById(R.id.empty_pantryId)
        textStatus = findViewById(R.id.empty_pantryStatus)



        fetchData(object: PantryCallback {
            override fun onCallBack(pantries: List<PantryEmpty>) {
                dataList = pantries as MutableList<PantryEmpty>
                mRecycleView.adapter = pantryAdapter
                pantryAdapter.notifyDataSetChanged()
                Log.d("FETCHING", "Successfully retreive history to dataList")

            }
        })


    }

    private fun fetchData(callback: PantryCallback) {
        //fetching data from database
        database = FirebaseDatabase.getInstance()
        resultRef = database.getReference()
        resultRef.child("Empty Pantry").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //get all children in this level
                val chidren: Iterable<DataSnapshot> = snapshot.child("pantryId").children
                //for each items
                dataList.clear()
                for(child: DataSnapshot in chidren){
                    val pantryResult: PantryEmpty? = child.getValue(PantryEmpty::class.java)
                    pantryResult?.let { dataList.add(it) }
                    Log.d("fetching", "pantryID : ${pantryResult?.pantryID} pantryStatus : ${pantryResult?.status}")

                }
            callback.onCallBack(dataList)


            }

        })
    }


}



