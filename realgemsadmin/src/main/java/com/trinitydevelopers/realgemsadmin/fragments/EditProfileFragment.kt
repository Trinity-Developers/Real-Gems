package com.trinitydevelopers.realgemsadmin.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentEditProfileBinding
import com.trinitydevelopers.realgemsadmin.databinding.FragmentProfileBinding
import com.trinitydevelopers.realgemsadmin.pojos.Profile
import java.util.UUID

class EditProfileFragment : Fragment() {
private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var currentUserId = "currentUserId"
    private var currentProfileImageUrl: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentEditProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        binding.progressBar2.visibility = View.GONE

        // Load existing profile data for editing
        loadProfileData()

        binding.editProfileImg.setOnClickListener {
            selectImage()
        }

        binding.btnEditProfileSave.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
            binding.btnEditProfileSave.isEnabled = false
            saveProfileChanges()
        }
    }

    private fun loadProfileData() {
        // Fetch profile data from Firestore and populate UI elements for editing
        firestore.collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject<Profile>()
                    // Bind profile data to UI elements for editing
                    Picasso.get().load(profile?.profileImageUrl).into(binding.editProfileImg)
                    binding.edtEditProfileName.setText(profile?.name)
                    binding.edtEditProfileContact.setText(profile?.contact)
                    binding.edtEditProfileAddress.setText(profile?.address)
                    currentProfileImageUrl = profile?.profileImageUrl ?: ""
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e(TAG, "Error loading profile", exception)
            }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUri = uri
                binding.editProfileImg.setImageURI(uri)
            }
        }
    }


    private fun saveProfileChanges() {
        val name = binding.edtEditProfileName.text.toString().trim()
        val contact = binding.edtEditProfileContact.text.toString().trim()
        val address = binding.edtEditProfileAddress.text.toString().trim()

        if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            // Handle empty fields
            Toast.makeText(requireContext(), "Enter all details", Toast.LENGTH_SHORT).show()
            binding.progressBar2.visibility = View.GONE
            binding.btnEditProfileSave.isEnabled = true
            return
        }

        // Upload image to Firebase Storage
        imageUri?.let { uri ->
            val imageRef = storageRef.child(UUID.randomUUID().toString())
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, get its download URL
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Save profile data to Firestore
                        saveProfileToFirestore(name, contact, address, downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error uploading image", e)
                    binding.progressBar2.visibility = View.GONE
                    binding.btnEditProfileSave.isEnabled = true
                    // Handle image upload error
                    Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            // Save profile data without image change
            saveProfileToFirestore(name, contact, address, currentProfileImageUrl)
        }
    }

    private fun saveProfileToFirestore(name: String, contact: String, address: String, profileImageUrl: String) {
        val updatedProfile = Profile(name, contact, address, profileImageUrl)
        firestore.collection("users")
            .document(currentUserId)
            .set(updatedProfile)
            .addOnSuccessListener {
                // Profile updated successfully
                binding.progressBar2.visibility = View.GONE
                binding.btnEditProfileSave.isEnabled = true
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating profile", e)
                binding.progressBar2.visibility = View.GONE
                binding.btnEditProfileSave.isEnabled = true
                // Handle error
                Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        private const val TAG = "EditProfileFragment"
    }
}
