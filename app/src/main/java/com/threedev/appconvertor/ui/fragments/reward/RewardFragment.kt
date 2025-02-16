package com.threedev.appconvertor.ui.fragments.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.threedev.appconvertor.databinding.FragmentRewardBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RewardFragment : Fragment() {

    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private val maxProgress = 100

    private var currentProgress = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rewardViewModel =
            ViewModelProvider(this).get(RewardViewModel::class.java)

        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize progress bar and progress percentage TextView
        progressBar = binding.rewardProgressBar
        progressText = binding.progressPercentage

        // Restore progress from saved instance state
        if (savedInstanceState != null) {
            currentProgress = savedInstanceState.getInt("currentProgress", 0)
        }

        // Set initial progress text
        updateProgress()

        // Reward ad watch button click listener
        val watchAdButton: Button = binding.btnWatchAd
        watchAdButton.setOnClickListener {
            // Increase progress by 10%
            val newProgress = currentProgress + 10

            if (newProgress <= maxProgress) {
                currentProgress = newProgress
                updateProgress()
            }
        }

        return root
    }

    private fun updateProgress() {

        progressBar.progress = currentProgress
        progressText.text = "$currentProgress%"

        progressBar.post {
            val progressWidth = progressBar.width
            val progressOffset = (progressWidth * (currentProgress / 100f)).toInt()


            val layoutParams = progressText.layoutParams as RelativeLayout.LayoutParams

            val textWidth = progressText.width
            layoutParams.leftMargin = progressOffset - (textWidth / 2)

            layoutParams.leftMargin = layoutParams.leftMargin.coerceAtLeast(0)
                .coerceAtMost(progressWidth - textWidth)

            progressText.layoutParams = layoutParams
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current progress to restore it later
        outState.putInt("currentProgress", currentProgress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


