package com.threedev.appconvertor.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.threedev.appconvertor.R
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from the arguments
        val name = arguments?.getString("name") ?: "Unknown"
        val phone = arguments?.getString("phone") ?: "Unknown"
        val dob = arguments?.getLong("dob") ?: 0L

        // Format Date of Birth
        val dobFormatted = if (dob > 0) {
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(dob))
        } else {
            "Unknown"
        }

        // Update the UI
        view.findViewById<TextView>(R.id.user_name_value).text = name
        view.findViewById<TextView>(R.id.phone_number_value).text = phone
        view.findViewById<TextView>(R.id.date_of_birth_value).text = dobFormatted
    }
}


