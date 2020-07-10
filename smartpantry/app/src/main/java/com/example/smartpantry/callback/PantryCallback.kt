package com.example.smartpantry.callback

import com.example.smartpantry.Model.PantryEmpty

public interface PantryCallback {
    fun onCallBack(pantries: List<PantryEmpty>)
}