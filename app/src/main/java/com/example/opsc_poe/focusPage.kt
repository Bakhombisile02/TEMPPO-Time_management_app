package com.example.opsc_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityFocusPageBinding
import com.example.opsc_poe.databinding.ActivityPiechartBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class focusPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityFocusPageBinding
    private val database = Firebase.database
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val ref1 = database.getReference("users").child(userId!!).child("TimeSheet")
    private lateinit var Timeselection : String
    private lateinit var CurrentTime : String
    private lateinit var label: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.navToolbarFocus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutFocus,binding.navToolbarFocus,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutFocus.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)
        label = findViewById<TextView>(R.id.RecordLabel)
        //-----------------------------------------------------------------------------------------------
        // Read from the database
        ref1.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList: MutableList<String> = mutableListOf()

                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.key
                    category?.let {
                        categoryList.add(it)
                    }
                }

                val spinner = findViewById<Spinner>(R.id.spinner)
                val adapter = ArrayAdapter(this@focusPage, android.R.layout.simple_spinner_item, categoryList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        Timeselection = selectedItem.toString()

                        val childKey = Timeselection

                        val childRef = ref1.child(childKey)

                        childRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val value = snapshot.child("recordedTime").getValue(String::class.java)
                                    CurrentTime = value.toString()
                                    label.text = value.toString()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        Toast.makeText(applicationContext, "Please select an item", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val Rbutton = findViewById<Button>(R.id.recordButton)
        Rbutton.setOnClickListener{
        val EditHours = findViewById<EditText>(R.id.txtHours)
            val txth = EditHours.text.toString()
            val total = CurrentTime.toInt() + txth.toInt()
            label.text = total.toString()
            val childKey = Timeselection

            val childRef = ref1.child(childKey)

            val updateMap = HashMap<String, Any>()
            updateMap["recordedTime"] = total.toString()

            childRef.updateChildren(updateMap)
                .addOnSuccessListener {
                    // The value has been updated successfully
                    Toast.makeText(applicationContext, "Recorded", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { error ->
                    // Handle the error if the update fails
                    Toast.makeText(applicationContext, "Error updating value: ${error.message}", Toast.LENGTH_SHORT).show()

                }

        }
        //-----------------------------------------------------------------------------------------------
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
        binding.drawerLayoutFocus.closeDrawer(GravityCompat.START)
        return true;
    }
}
//----------------------------------------End of File ------------------------------------------------------//