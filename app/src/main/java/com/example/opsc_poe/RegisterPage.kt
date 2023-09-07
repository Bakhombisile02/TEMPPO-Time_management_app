package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.example.opsc_poe.databinding.ActivityLoginPageBinding
import com.example.opsc_poe.databinding.ActivityRegisterPageBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class RegisterPage : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener  {

    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityRegisterPageBinding
    private lateinit var buttonReg : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var progressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.navToolbarReg)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutReg,binding.navToolbarReg,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutReg.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)
        buttonReg = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.RegProgressbar)

        buttonReg.setOnClickListener{
            progressBar.isVisible = true
            ValidateData()

        }


    }
    //----------------------------------------------------------------------------------------------
    //Method for navigation (openIntent method is in IntentClass.kt File)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.u_nav_HP -> openIntent(applicationContext, "", MainActivity::class.java)
            //    R.id.u_nav_About -> openIntent(applicationContext, "", pieChart::class.java)
            R.id.u_nav_Log -> openIntent(applicationContext, "", LoginPage::class.java)
            R.id.u_nav_Regist -> openIntent(applicationContext, "", RegisterPage::class.java)
        }
        binding.drawerLayoutReg.closeDrawer(GravityCompat.START)
        return true;
    }

    //Method to Validate data and exception handling
    fun ValidateData()
    {
        val nam = findViewById<EditText>(R.id.txtRegName)
        var name= nam.text.toString()
        val sur = findViewById<EditText>(R.id.txtRegSurname)
        var surname = sur.text.toString()
        val em = findViewById<EditText>(R.id.txtRegEmail)
        var email = em.text.toString()
        val num = findViewById<EditText>(R.id.txtRegCellphone)
        var number = num.text.toString()
        val pass = findViewById<EditText>(R.id.txtRegPassword)
        var password = pass.text.toString()
        val conp = findViewById<EditText>(R.id.txtRegConfirmPassword)
        var conpass = conp.text.toString()



        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(number)|| TextUtils.isEmpty(password)|| TextUtils.isEmpty(conpass))
        {
            progressBar.isVisible = false
            Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_SHORT).show()
            return;
        }
        if(password != conpass)
        {
            progressBar.isVisible = false
            Toast.makeText(this,"Passwords do not match!",Toast.LENGTH_SHORT).show()
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            progressBar.isVisible = false
            Toast.makeText(this,"Email not Valid",Toast.LENGTH_SHORT).show()
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressBar.isVisible = false
                    val user = auth.currentUser
                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT,).show()
                    openIntent(applicationContext,"",LoginPage::class.java)
                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.isVisible = false
                    Toast.makeText(this, "Authentication failed \nSet stronger Password or email is already used", Toast.LENGTH_SHORT,).show()
                }
            }

    }

}
//----------------------------------------End of File ------------------------------------------------------//