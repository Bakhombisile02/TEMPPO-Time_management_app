package com.example.opsc_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.example.opsc_poe.databinding.ActivityDashboardBinding
import com.example.opsc_poe.databinding.ActivityLoginPageBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityLoginPageBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var progressBar : ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.navToolbarLog)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayout,binding.navToolbarLog,R.string.open_nav,R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)

        val btnlog = findViewById<Button>(R.id.btnLogin)
        progressBar = findViewById(R.id.LogProgressbar)

        btnlog.setOnClickListener{
            progressBar.isVisible = true
            validateData()
        }
    }
    //---------------------------------------------------------------------------------------------------------//
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.u_nav_HP -> openIntent(applicationContext, "", MainActivity::class.java)
        //    R.id.u_nav_About -> openIntent(applicationContext, "", pieChart::class.java)
            R.id.u_nav_Log -> openIntent(applicationContext, "", LoginPage::class.java)
            R.id.u_nav_Regist -> openIntent(applicationContext, "", RegisterPage::class.java)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }
//---------------------------------------------------------------------------------------------------------//
    //firebase authentication and exception handling
    fun validateData()
    {
        val user = findViewById<EditText>(R.id.txtUsername)
        val pass = findViewById<EditText>(R.id.txtPassword)


        if(TextUtils.isEmpty(user.text.toString()) || TextUtils.isEmpty(pass.text.toString()))
        {
            Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_SHORT).show()
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(user.text.toString()).matches())
        {
            Toast.makeText(this,"Email not Valid",Toast.LENGTH_SHORT).show()
            return;
        }

        auth.signInWithEmailAndPassword(user.text.toString(), pass.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.isVisible = false
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    openIntent(applicationContext,"",LoggedHomePage::class.java)


                } else {
                    progressBar.isVisible = false
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }
    }


}
//----------------------------------------End of File ------------------------------------------------------//