package com.threedev.appconvertor.ui.fragments.convertapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.threedev.appconvertor.R
import com.threedev.appconvertor.data.models.MainViewModel
import com.threedev.appconvertor.data.models.WebBuilderApkInfo
import com.threedev.appconvertor.databinding.FragmentConvertappBinding
import com.threedev.appconvertor.ui.utils.Constants
import java.util.Calendar

class ConvertAppFragment : Fragment() {

    private lateinit var binding: FragmentConvertappBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Constants
    private val PICK_IMAGE_REQUEST = 1
    private val MAX_IMAGE_SIZE = 2 * 1024 * 1024
    private val viewModel: ConvertViewModel by viewModels()
    private val mainVM: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConvertappBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val uploadIconContainer: FrameLayout = binding.flUploadicone

        val imageView = ImageView(requireContext())
        imageView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        uploadIconContainer.addView(imageView)

        uploadIconContainer.setOnClickListener {
            openGallery()
        }

        binding.btnSave.setOnClickListener {
            if (!validateName()) {
                return@setOnClickListener
            }
            if (!validatePackageName()) {
                return@setOnClickListener
            }
            if (!validateURL()) {
                return@setOnClickListener
            }

            if (viewModel.onUploadImage().value?.data == null) {
                Toast.makeText(requireContext(), "Please upload image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val appData = WebBuilderApkInfo(
                "",
                binding.etAppName.text.toString(),
                "com.example." + binding.packageName.text.toString().trim(),
                binding.webUrl.text.toString(),
                viewModel.onUploadImage().value?.data!!,
                Calendar.getInstance().timeInMillis,
                Calendar.getInstance().timeInMillis,
                status = Constants.STATUS_PENDING
            )
            saveAppData(appData)
        }

        viewModel.onUploadImage().observe(viewLifecycleOwner) { resource ->
            when (resource.code) {
                500 -> {
                    //loading show loading
                }

                200 -> {
                    //success hide loader
                    //filterAppData(Constants.STATUS_PENDING)
                    Toast.makeText(requireContext(), "Upload successfull", Toast.LENGTH_SHORT)
                        .show()

                }

                else -> {
                    //error hide loader and show message
                }
            }
        }
        binding.etAppName.addTextChangedListener(MyTextWatcher(binding.etAppName))
        binding.packageName.addTextChangedListener(MyTextWatcher(binding.packageName))
        binding.webUrl.addTextChangedListener(MyTextWatcher(binding.webUrl))


    }


    // Function to open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the selected image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            if (imageUri != null) {
                // Check image size
                if (isImageSizeValid(imageUri)) {
                    val imageView = binding.flUploadicone.getChildAt(0) as ImageView
                    imageView.setImageURI(imageUri)

                    viewModel.uploadImage(imageUri)


                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select an image smaller than 2MB.",
                        Toast.LENGTH_SHORT
                    ).show()
                    openGallery()
                }
            }
        }
    }

    // Function to check image size
    private fun isImageSizeValid(imageUri: Uri): Boolean {
        val cursor = context?.contentResolver?.query(imageUri, null, null, null, null)

        var sizeValid = false
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (it.moveToFirst()) {
                val fileSize = it.getLong(sizeIndex)
                sizeValid = fileSize <= MAX_IMAGE_SIZE
            }
        }
        return sizeValid
    }

    private fun saveAppData(appInfo: WebBuilderApkInfo) {
        val currentUser = auth.currentUser
        val docRef = db.collection(currentUser!!.uid).document()
        appInfo.apply {
            docId = docRef.id
        }
        docRef.set(appInfo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "App saved successfully", Toast.LENGTH_SHORT)
                    .show()
                mainVM.getAllData().value?.data?.builderData?.add(appInfo)
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error saving app: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun isValidWebUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    private fun validateName(): Boolean {
        if (binding.etAppName.getText().toString().trim().isEmpty()) {
            binding.inputLayoutAppName.setError(getString(R.string.error_field_require))
            requestFocus(binding.etAppName)
            return false
        } else {
            binding.inputLayoutAppName.isErrorEnabled = false
        }
        return true
    }

    private fun validatePackageName(): Boolean {
        val packageName = binding.packageName.text.toString()
        if (packageName.isEmpty()) {
            binding.packageName.error = getString(R.string.error_field_require)
            requestFocus(binding.etAppName)
            return false
        } else if (packageName.any { it.isDigit() }) {
            binding.packageName.error = "Package Name should not contain numbers"
            requestFocus(binding.etAppName)
            return false
        } else {
            binding.packageName.error = null
        }
        return true
    }

    private fun validateURL(): Boolean {
        if (binding.webUrl.getText().toString().trim().isEmpty()) {
            binding.inputLayoutUrl.setError(getString(R.string.error_field_require))
            requestFocus(binding.webUrl)
            return false
        } else if (!isValidWebUrl(binding.webUrl.text.toString())) {
            binding.inputLayoutUrl.setError("Please enter a valid URL")
            requestFocus(binding.webUrl)
            return false
        } else {
            binding.inputLayoutUrl.isErrorEnabled = false
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    inner class MyTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.et_app_name -> {
                    validateName()
                }

                R.id.package_name -> {
                    validatePackageName()
                }

                R.id.web_url -> validateURL()
            }
        }
    }


}

