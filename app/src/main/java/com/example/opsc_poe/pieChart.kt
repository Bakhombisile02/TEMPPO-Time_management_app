package com.example.opsc_poe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityDashboardBinding
import com.example.opsc_poe.databinding.ActivityPiechartBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class pieChart : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    //Binding for navigation drawer and toolbar

    private lateinit var binding: ActivityPiechartBinding

    private val database = Firebase.database
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val ref1 = database.getReference("users").child(userId!!).child("TimeSheet")
    private val database2 = Firebase.database
    private val ref2 = database2.getReference("users").child(userId!!).child("Study Goals")
    private val keyList = ArrayList<String>() // ArrayList to store the key names
    private val valueList = ArrayList<String>()
    private lateinit var pieChart: PieChart
    private lateinit var maxGoalSet: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPiechartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.navToolbarPie)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayoutPie,
            binding.navToolbarPie,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayoutPie.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)

        pieChart = findViewById(R.id.pie_chart)
        getTimesheet()
        getMaxGoal()

    }

    //Dummy data in place for final project
    fun filldata(pieChart: PieChart) {


        val list: ArrayList<PieEntry> = ArrayList()

        for (i in 0 until keyList.size) {
            val key = keyList[i]
            val value = valueList[i]

            try {
                val keyFloat = value.toFloat()
                list.add(PieEntry(keyFloat, key))
            } catch (e: NumberFormatException) {
                // Handle the case when the key cannot be parsed as a float
                // You can choose to skip this entry or handle it in a desired way
            }
        }

        val pieDataSet = PieDataSet(list, "TimeSheet Hours")
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)

        pieDataSet.valueTextSize = 15f
        pieDataSet.valueTextColor = Color.BLACK
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        pieChart.description.text = ""
        pieChart.centerText = "TimeSheet"
        pieChart.animateY(2000)
    }

    //----------------------------------------------------------------------------------------------
    //Method for navigation (openIntent method is in IntentClass.kt File)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.l_nav_HP -> openIntent(applicationContext, "", LoggedHomePage::class.java)
            R.id.l_nav_Calendar -> openIntent(applicationContext, "", Calendar::class.java)
            R.id.l_nav_Dash -> openIntent(applicationContext, "", Dashboard::class.java)
            R.id.l_nav_bgraph -> openIntent(
                applicationContext,
                "",
                com.example.opsc_poe.pieChart::class.java
            )
            R.id.l_nav_addCategory -> openIntent(applicationContext, "", AddCategory::class.java)
            R.id.l_nav_Timesheet -> openIntent(applicationContext, "", TimeSheet::class.java)
            R.id.l_nav_viewTimesheet -> openIntent(applicationContext, "", ViewData::class.java)
            R.id.l_nav_pTimer -> openIntent(applicationContext, "", focusPage::class.java)
            R.id.l_nav_Settings -> openIntent(applicationContext, "", Dashboard::class.java)
            R.id.l_nav_Signout -> openIntent(
                applicationContext,
                "",
                MainActivity::class.java
            ).also {
                Firebase.auth.signOut()
            }

        }
        binding.drawerLayoutPie.closeDrawer(GravityCompat.START)
        return true;
    }

    fun getTimesheet() {

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key
                    val value = childSnapshot.child("recordedTime").getValue(String::class.java)

                    key?.let { keyList.add(it) }
                    value?.let { valueList.add(it) }
                    filldata(pieChart)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        }

        ref1.addListenerForSingleValueEvent(valueEventListener)


    }

    fun goalData() {
        val textData = findViewById<TextView>(R.id.goalTextView)
        for (i in 0 until valueList.size)
        {
            var status = ""
            val v = valueList[i]

            if (v.toInt() < maxGoalSet.toInt())
            {
                status = ": Goal not reached"
            } else {
                status = ": Goal reached"
            }
            textData.text =  textData.text.toString() + keyList[i].toString() + status + "\n"

        }
    }

    fun getMaxGoal() {


        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val maxDaily = dataSnapshot.child("maxDaily").getValue(Int::class.java)
                maxGoalSet = maxDaily.toString()
                goalData()

            }

            override fun onCancelled(databaseError: DatabaseError) {


            }
        })
    }
}
//----------------------------------------End of File ------------------------------------------------------//