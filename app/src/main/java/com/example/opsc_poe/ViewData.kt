package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat

import com.example.opsc_poe.databinding.ActivityViewDataBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewData : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityViewDataBinding
    private val database = Firebase.database
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val myRef1 = database.getReference("users").child(userId!!).child("TimeSheet")


    private lateinit var listView: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDataBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.navToolbarView)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutView,binding.navToolbarView,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutView.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)

       // listView2 = findViewById(R.id.tableLayoutView)

        listView = findViewById(R.id.tableLayoutView)

        myRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Add rows dynamically
                for (timeSheetSnapshot in snapshot.children) {
                    val timeData: TimeSheetData? =
                        timeSheetSnapshot.getValue(TimeSheetData::class.java)
                    timeData?.let {
                        val rowData = arrayOf(
                            it.category.toString(),
                            it.description.toString(),
                            it.startDate.toString(),
                            it.endDate.toString(),
                            it.photo.toString()
                        )

                        val row = TableRow(this@ViewData)
                        val layoutParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        )
                        row.layoutParams = layoutParams

                        for (i in rowData.indices) {
                            val cell = TextView(this@ViewData)
                            cell.text = rowData[i]
                            cell.setPadding(16, 16, 16, 16)
                            row.addView(cell)
                        }

                        listView.addView(row)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    //----------------------------------------------------------------------------------------------
    //Method for navigation (openIntent method is in IntentClass.kt File)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.l_nav_HP -> openIntent(applicationContext, "", LoggedHomePage::class.java)
            R.id.l_nav_Calendar -> openIntent(applicationContext, "", Calendar::class.java)
            R.id.l_nav_Dash -> openIntent(applicationContext, "", Dashboard::class.java)
            R.id.l_nav_bgraph -> openIntent(applicationContext, "", pieChart::class.java)
            R.id.l_nav_addCategory -> openIntent(applicationContext, "", AddCategory::class.java)
            R.id.l_nav_Timesheet -> openIntent(applicationContext, "", TimeSheet::class.java)
            R.id.l_nav_viewTimesheet -> openIntent(applicationContext, "", ViewData::class.java)
            R.id.l_nav_pTimer -> openIntent(applicationContext, "", focusPage::class.java)
            R.id.l_nav_Settings -> openIntent(applicationContext, "", Dashboard::class.java)
            R.id.l_nav_Signout -> openIntent(applicationContext, "", MainActivity::class.java).also {
                Firebase.auth.signOut()
            }

        }
        binding.drawerLayoutView.closeDrawer(GravityCompat.START)
        return true;
    }


}
//----------------------------------------End of File ------------------------------------------------------//