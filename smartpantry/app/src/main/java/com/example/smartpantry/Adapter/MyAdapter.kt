package com.example.smartpantry.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartpantry.GiverSelectPantryActivity
import com.example.smartpantry.Model.PantryEmpty
import com.example.smartpantry.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import helpers.MqttClient
import helpers.MqttGiver



// MyAdapter class will connect between UI component and data source that help us to fill data in UI component

class MyAdapter(internal var context: MutableList<PantryEmpty>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){ //, View.OnClickListener
            internal var txt_pantryID: TextView
            internal var txt_pantryStatus: TextView

            init{
                txt_pantryID = itemView.findViewById<TextView>(R.id.pantry_id_description)
                txt_pantryStatus = itemView.findViewById<TextView>(R.id.pantry_status_description)

//                itemView.setOnClickListener({
//                    val mIntent = Intent(itemView.context, GiverSelectPantryActivity::class.java)
//                    mIntent.putExtra("pantryIdAdapter", txt_pantryID.toString())
//                    itemView.context.startActivity(mIntent)
//                    Log.d("MyAdapter", "enter setOnclickListener & pantry ID is $txt_pantryID.toString()")
//
//                })
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return context.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txt_pantryID.text = context[position].pantryID
        holder.txt_pantryStatus.text = context[position].status
        Log.d("MyAdapter", "enter setOnclickListener $context[position].pantryID")

        // make recyclerview clickable so givers can click pantry id in recyclerview
        //after givers click, it will navigate givers to GiverSelectPantry
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(it: View?) {
                val mIntent = Intent(it!!.context, GiverSelectPantryActivity::class.java)
                mIntent.putExtra("pantryIdAdapter", context.get(position).pantryID )
                it.context.startActivity(mIntent)
                Log.d("MyAdapter", "enter setOnclickListener & pantry ID is " + context.get(position).pantryID)
            }
        })
    }


}

