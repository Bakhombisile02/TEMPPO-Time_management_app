package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityAddCategoryBinding

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddCategory : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityAddCategoryBinding
    private lateinit var catego : EditText

    private val database = Firebase.database //Database connection
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val myRef = database.getReference("users").child(userId!!).child("Category")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.navToolbarCat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutCat,binding.navToolbarCat,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutCat.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)
        val cat = findViewById<EditText>(R.id.txtCategoryName)
        val btn = findViewById<Button>(R.id.btnAdd)

     btn.setOnClickListener{
         if(TextUtils.isEmpty(cat.text.toString()))
         {
             Toast.makeText(this,"Please fill in a category name",Toast.LENGTH_SHORT).show()

         }
         else {
             val catname = findViewById<EditText>(R.id.txtCategoryName)
             var name = catname.text.toString().trim()
             myRef.push().setValue(name).addOnSuccessListener {
                 Toast.makeText(this, "Category Added", Toast.LENGTH_SHORT).show()
             }
         }
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
        binding.drawerLayoutCat.closeDrawer(GravityCompat.START)
        return true;
    }



}
//----------------------------------------End of File ------------------------------------------------------//