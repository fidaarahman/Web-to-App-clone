package com.threedev.appconvertor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.threedev.appconvertor.data.models.UserFormInfo
import com.threedev.appconvertor.helpers.SessionManager
import com.threedev.appconvertor.ui.activities.LoginActivity
import com.threedev.appconvertor.data.models.MainViewModel
import com.threedev.appconvertor.databinding.ActivityMainBinding

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var profileData: UserFormInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.show()

        if (SessionManager.getInt(SessionManager.APP_LIMIT_KEY, -10) == -10) {
            SessionManager.setInt(SessionManager.APP_LIMIT_KEY, 10)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)


        binding.bottomNavigation.setupWithNavController(navController)

        observeProfileData()


        viewModel.fetchAllData()
    }

    private fun observeProfileData() {
        viewModel.getAllData().observe(this) { resource ->
            when (resource.code) {
                500 -> {
                    //loading show loading
                }
                200 -> {
                    //success hide loader
                    profileData = resource.data?.profile

                }
                else -> {
                    //error hide loader and show message
                }
            }
        }

        viewModel.profileData.observe(this) { profileDataList ->
            if (profileDataList.isNotEmpty()) {
                profileData = profileDataList.first()
                Log.d("Profile Data", "Profile data loaded: $profileData")
            } else {
                Log.d("Profile Data", "No profile data found")
            }
        }

        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            Log.e("Firestore", error)
        })
    }



    private fun logoutUser() {

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)


        Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show()
        Log.d("Logout", "User logged out and redirected to login screen")
    }

    private fun openProfileFragment(name: String, phone: String, dob: String) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val dobAsLong = dob.toLongOrNull() ?: 0L

        val bundle = Bundle().apply {
            putString("name", name)
            putString("phone", phone)
            putLong("dob", dobAsLong)
        }

        navController.navigate(R.id.nav_profile, bundle)
    }
    /*
       *//* private fun updateNavHeader() {
        // Get header view
       // val headerView = binding.navView.getHeaderView(0)

        val tvUserName: TextView = headerView.findViewById(R.id.tv_title)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("name") ?: "User"
                        tvUserName.text = profileData?.name
                    } else {
                        tvUserName.text = profileData?.name
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("NavHeader", "Error fetching user data: ${exception.message}")
                    tvUserName.text = "User"
                }
        } else {
            tvUserName.text = "Guest"
        }
    }*/

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
