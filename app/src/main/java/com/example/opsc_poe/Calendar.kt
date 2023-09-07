package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityCalendarBinding
import com.example.opsc_poe.databinding.ActivityMainWithNavDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Calendar : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.navToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayout,binding.navToolbar,R.string.open_nav,R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)

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

        return true;
    }
}
//----------------------------------------End of File ------------------------------------------------------//