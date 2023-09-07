package com.example.opsc_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle


    //Method to open intent in navigation
    fun openIntent(context: Context, order: String, activityToOpen: Class<*>)
    {
        val intent = Intent(context, activityToOpen)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("order",order)
        context.startActivity(intent)

    }





