package com.example.opsc_poe

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.opsc_poe.databinding.ActivityPiechartBinding
import com.example.opsc_poe.databinding.ActivityTimeSheetBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class TimeSheet : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    //Database connection and paths
    private val database = Firebase.database
    private val database2 = Firebase.database
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val myRef = database.getReference("users").child(userId!!).child("Category")
    private val myRef1 = database2.getReference("users").child(userId!!).child("TimeSheet")

    //Binding for navigation drawer and toolbar
    private lateinit var binding : ActivityTimeSheetBinding
    private lateinit var submitbtn : Button
    private lateinit var IMG : ImageView
    private var imageURI : Uri? = null
   private lateinit var reference : StorageReference
    private lateinit var galler : Intent
    private lateinit var txtDescr : EditText

    private lateinit var startDate : String
    private lateinit var endDate : String
    private lateinit var Description : String
    private lateinit var Category : String
    private lateinit var RecordedTime : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.navToolbarTimesheet)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayoutTimeSheet,binding.navToolbarTimesheet,R.string.open_nav,R.string.close_nav)
        binding.drawerLayoutTimeSheet.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.bringToFront()
        binding.navView.setNavigationItemSelectedListener(this)


        txtDescr = findViewById(R.id.txtTimeDesciption)
        IMG = findViewById(R.id.imageView)
        reference = FirebaseStorage.getInstance().getReference()
        submitbtn = findViewById(R.id.btnTimeSubmit)


        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList: MutableList<String> = mutableListOf()

                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(String::class.java)
                    category?.let {
                        categoryList.add(it)
                    }
            }

                    val spinner = findViewById<Spinner>(R.id.spinner)
                    val adapter = ArrayAdapter(this@TimeSheet, android.R.layout.simple_spinner_item, categoryList)
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
                        val labe = findViewById<TextView>(R.id.catLabel)
                        labe.text = selectedItem.toString()
                        Category = selectedItem.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        Toast.makeText(applicationContext, "Please select an item", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        IMG.setOnClickListener{

            galler = Intent()
            galler.setAction(Intent.ACTION_GET_CONTENT)
            galler.setType("image/*")
            startActivityForResult(galler,2)
        }
//--------------------------------------------------------------------------------------------------------
        submitbtn.setOnClickListener{

            this.Description = txtDescr.text.toString()

            if (imageURI != null) {
                if (TextUtils.isEmpty(txtDescr.text.toString())) {
                    Toast.makeText(this, "Please fill in the description", Toast.LENGTH_SHORT).show()
                } else {
                    uploadToFirebase(imageURI!!)

                    val customChildName = Description // Provide your desired custom name here
                    RecordedTime = "0"
                    val data = TimeSheetData(startDate, endDate, Description, imageURI.toString(), Category, RecordedTime)
                    val childRef = myRef1.child(customChildName)

                    childRef.setValue(data).addOnSuccessListener {
                        Toast.makeText(this, "TimeSheet Added", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
            }
        }
//-----------------------------------------------------------------------------------------------------------

        val btnDateRangePicker = findViewById<MaterialButton>(R.id.btnDateRangePicker)


        btnDateRangePicker.setOnClickListener {
            val cal = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Date").build()
            cal.show(supportFragmentManager,"date_range_picker")

          cal.addOnPositiveButtonClickListener { dateRange ->
              val startD = dateRange.first
              val endD = dateRange.second

              val dateFormat = SimpleDateFormat("yyyy-MM-dd")
              val formattedDateStart = dateFormat.format(startD)
              val formattedDateEnd = dateFormat.format(endD)
              val label = findViewById<TextView>(R.id.dateLabel)

              this.startDate = formattedDateStart.toString()
              this.endDate = formattedDateEnd.toString()

              label.text = "Start Date: $formattedDateStart" + "\nEnd Date: $formattedDateEnd"
              Toast.makeText(this, "$formattedDateStart $formattedDateEnd", Toast.LENGTH_SHORT).show()
          }
        }
    }
//------------------------------------------------------------------------------------------------------------
    //method to get URI from photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==2 && resultCode == RESULT_OK && data != null){
            this.imageURI = data.data!!
            IMG.setImageURI(data.data)

        }
    }
    //------------------------------------------------------------------------------------------------------
    //Method to upload to firebase
    fun uploadToFirebase(uri: Uri)
    {
        val fileRef: StorageReference = reference.child("${System.currentTimeMillis()}.${getFileExtension(uri)}")

        fileRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully
            Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
            // You can retrieve the download URL of the uploaded image using taskSnapshot.storage.downloadUrl
            // For example: val downloadUrl = taskSnapshot.storage.downloadUrl
        }
            .addOnFailureListener { exception ->
                // Failed to upload the image
                Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

    }
//------------------------------------------------------------------------------------------------------------
    fun getFileExtension(muri: Uri): String?{
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()

     return mime.getExtensionFromMimeType(cr.getType(muri))
    }
//--------------------------------------------------------------------------------------------------
    fun getCategorySelected(){

        val spinner = findViewById<Spinner>(R.id.spinner)
        var data = spinner.selectedItem.toString()
        Category = data
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
        binding.drawerLayoutTimeSheet.closeDrawer(GravityCompat.START)
        return true;

    }
}
//----------------------------------------End of File ------------------------------------------------------//



