package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.slider.LabelFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.slider.Slider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ValueEventListener



class Dashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Binding for navigation drawer and toolbar
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var minDailySeekBar: SeekBar
    private lateinit var maxDailySeekBar: SeekBar
    private lateinit var selectedValuesTextView: TextView

    private val database = Firebase.database //Database connection
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val myRef = database.getReference("users").child(userId!!).child("Study Goals")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        switchDarkMode = findViewById(R.id.switch_dark_mode)
        minDailySeekBar = findViewById<SeekBar>(R.id.minDailySeekBar)
        maxDailySeekBar = findViewById<SeekBar>(R.id.maxDailySeekBar)
        selectedValuesTextView = findViewById(R.id.selectedValuesTextView)
        val btn = findViewById<Button>(R.id.btnGoalSet)

        setSupportActionBar(binding.navToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.navToolbar, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Apply dark mode theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Apply light mode theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }



        // Update the TextView (selectedValuesTextView) with the selected values
        minDailySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedValuesTextView.text = "Study Goals: $progress - ${maxDailySeekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update the TextView (selectedValuesTextView) with the selected values
        maxDailySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedValuesTextView.text = "Goals: ${minDailySeekBar.progress} - $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btn.setOnClickListener{
            val minDaily = minDailySeekBar.progress
            val maxDaily = maxDailySeekBar.progress

            // Save the values to the database
            myRef.child("minDaily").setValue(minDaily)
            myRef.child("maxDaily").setValue(maxDaily)

            Toast.makeText(this, "Values saved to the database", Toast.LENGTH_SHORT).show()
        }

        // Read values from the database and set them to the SeekBar
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val minDaily = dataSnapshot.child("minDaily").getValue(Int::class.java)
                val maxDaily = dataSnapshot.child("maxDaily").getValue(Int::class.java)

                minDaily?.let {
                    minDailySeekBar.progress = it
                }

                maxDaily?.let {
                    maxDailySeekBar.progress = it
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database read error
                Toast.makeText(this@Dashboard, "Failed to read values from the database", Toast.LENGTH_SHORT).show()
            }
        })

    }
    //----------------------------------------------------------------------------------------------
    //Method for navigation (openIntent method is in IntentClass.kt File)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
//----------------------------------------End of File ------------------------------------------------------//