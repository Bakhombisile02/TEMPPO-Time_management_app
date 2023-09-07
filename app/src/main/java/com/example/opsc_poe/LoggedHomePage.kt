package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.opsc_poe.databinding.ActivityLoggedHomePageBinding
import com.example.opsc_poe.databinding.ActivityLoginPageBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoggedHomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityLoggedHomePageBinding

    private lateinit var user : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.navToolbarHP)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutHP,binding.navToolbarHP,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutHP.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)
        val btnCat = findViewById<Button>(R.id.btnGoCategory)
        val btnTime = findViewById<Button>(R.id.btnGoTimeSheet)

        btnCat.setOnClickListener{
            openIntent(applicationContext, "", AddCategory::class.java)
        }
        btnTime.setOnClickListener{
            openIntent(applicationContext, "", TimeSheet::class.java)
        }
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