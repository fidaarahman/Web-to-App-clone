package com.threedev.appconvertor.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.threedev.appconvertor.MainActivity
import com.threedev.appconvertor.R
import com.threedev.appconvertor.data.models.UserFormInfo
import com.threedev.appconvertor.databinding.ActivityFormBinding
import java.util.Calendar

class FormActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFormBinding

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username=intent.getStringExtra("USER_NAME")?:""

        if(username.isNotEmpty()){
            binding.etName.setText(username)
        }

        binding=ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener{
            saveProfileData()
        }

    }
    private fun saveProfileData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val email = currentUser.email

            val name = binding.etName.text.toString().trim()
            val phone =binding.etPhone.text.toString().trim()
            val country = binding.etCountry.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return
            }
            val docRef = db.collection(currentUser.uid).document()
            val profileData = UserFormInfo(name, phone,country)
            docRef.set(profileData).addOnSuccessListener {
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(
                    this,
                    "App data saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

}
