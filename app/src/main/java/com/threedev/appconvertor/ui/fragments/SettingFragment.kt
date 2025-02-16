package com.threedev.appconvertor.ui.fragments

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.threedev.appconvertor.MainActivity
import com.threedev.appconvertor.R
import com.threedev.appconvertor.databinding.FragmentSettingBinding
import com.threedev.appconvertor.ui.activities.LoginActivity
import firebase.com.protolitewrapper.BuildConfig

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.llShareapp.setOnClickListener {
            onClickShareApp()
        }

        binding.llPrivacypolicy.setOnClickListener{
            onClickPrivacyPolicy()
        }
        binding.llMoreapps.setOnClickListener{
            moreApps()
        }
        binding.llLogout.setOnClickListener{
            showLogoutDialog()
        }


    }
    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to logout?")
        alertDialog.setPositiveButton("Logout") { dialog, _ ->

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()


            navigateToLogin()

            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            // Dismiss dialog
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun navigateToLogin() {
     val intent=Intent(requireContext(),LoginActivity::class.java)
        startActivity(intent)
        Toast.makeText(requireContext(), "Redirecting to login screen...", Toast.LENGTH_SHORT).show()
    }
    private fun onClickShareApp() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.share)
        builder.setMessage(R.string.share_app_message)
        builder.setPositiveButton(R.string.shr) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
            val intent =
                Intent(Intent.ACTION_SEND)
            val shareBody =
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, "Share Using"))
        }
        builder.setNegativeButton(
            R.string.cancel
        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
        builder.show()
    }
    private fun moreApps() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.more_app)
        builder.setMessage(R.string.moreapp_des)
        builder.setPositiveButton(R.string.redirect) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/search?q=3DevTech&c=apps")
                )
            )
        }
        builder.setNegativeButton(
            R.string.cancel
        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
        builder.show()
        /* val dialogView = layoutInflater.inflate(R.layout.more_app_layout, null)
         val builder = AlertDialog.Builder(requireContext())
             .setView(dialogView)
             .setCancelable(true)

         val messageTextView: TextView = dialogView.findViewById(R.id.tv_alert_message)
         val yesButton: Button = dialogView.findViewById(R.id.yes)
         val noButton: Button = dialogView.findViewById(R.id.confirm_no_delete_button)
         val dialog = builder.create()

         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window!!.setDimAmount(0.6f)
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.show()
         messageTextView.text = "You will be redirected to our Play Store account.\nAre you sure?"
         yesButton.setOnClickListener {
             try {
                 startActivity(
                     Intent(
                         Intent.ACTION_VIEW,
                         Uri.parse("market://search?q=pub:" + getString(R.string.developer_account_link))
                     )
                 )
             } catch (ex: ActivityNotFoundException) {
                 startActivity(
                     Intent(
                         Intent.ACTION_VIEW,
                         Uri.parse("https://play.google.com/store/search?q=3DevTech&c=apps")
                     )
                 )
             }
             dialog.dismiss()
         }
         noButton.setOnClickListener {
             dialog.dismiss() // Dismiss the dialog
         }

         dialog.show()

 */
    }
    private fun onClickPrivacyPolicy() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.privacy_policy)
        builder.setMessage(R.string.privacypolicy_Des)
        builder.setPositiveButton(R.string.redirect) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
            startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(getString(R.string.policy_url))
                )
            )
        }
        builder.setNegativeButton(
            R.string.cancel
        ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
        builder.show()
    }
}
