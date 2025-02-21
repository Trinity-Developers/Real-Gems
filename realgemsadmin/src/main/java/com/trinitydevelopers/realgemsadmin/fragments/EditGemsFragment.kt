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
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentEditGemsBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems


class EditGemsFragment : Fragment() {

    private lateinit var binding:FragmentEditGemsBinding
    private var selectedGem: Gems? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentEditGemsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve selected gem data from arguments
        arguments?.let {
            selectedGem = it.getSerializable("selectedGem") as? Gems
        }


        // Setup spinners and populate with data
        setupSpinners()

        // Populate EditText fields with selected gem data if available
        selectedGem?.let { gem ->
            binding.editTextOrigin.setText(gem.origin)
            binding.editTextColor.setText(gem.color)
            binding.editTextCarats.setText(gem.carats.toString())

            // Load images into image views using Picasso
            val imageViews = listOf(
                binding.EditGemImage1,
                binding.imageView2,
                binding.imageView3,
                binding.imageView4
            )
            gem.imageUrls.forEachIndexed { index, imageUrl ->
                if (index < imageViews.size) {
                    Picasso.get().load(imageUrl).into(imageViews[index])
                }
            }
        }

        // Setup click listeners for image views
        binding.EditGemImage1.setOnClickListener { pickImageForImageView(0) }
        binding.imageView2.setOnClickListener { pickImageForImageView(1) }
        binding.imageView3.setOnClickListener { pickImageForImageView(2) }
        binding.imageView4.setOnClickListener { pickImageForImageView(3) }

        // Setup save button click listener
        binding.btnEditGems.setOnClickListener {
            selectedGem?.let {
                updateGemData(it)
            }
        }

    }


    private fun setupSpinners() {
        setupSpinner(binding.editName, "names",selectedGem?.nameId)
        setupSpinner(binding.editCut, "cuts",selectedGem?.cutId)
        setupSpinner(binding.editShape, "shapes",selectedGem?.shapeId)
        setupSpinner(binding.editComposition, "compositions",selectedGem?.compositionId)
        setupSpinner(binding.editTreatment, "treatments",selectedGem?.treatmentId)

        // Pre-select existing data if available
        selectedGem?.let { gem ->
            binding.editName.setSelection(getSpinnerIndex(binding.editName, gem.nameId))
            binding.editComposition.setSelection(getSpinnerIndex(binding.editCut, gem.cutId))
            binding.editShape.setSelection(getSpinnerIndex(binding.editShape, gem.shapeId))
            binding.editComposition.setSelection(getSpinnerIndex(binding.editComposition, gem.compositionId))
            binding.editTreatment.setSelection(getSpinnerIndex(binding.editTreatment, gem.treatmentId))
        }
    }


    private fun setupSpinner(spinner: Spinner, collection: String, selectedItem: String?) {
        firestore.collection("lists").document(collection).collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val items = mutableListOf<String>()

                for (document in documents) {
                    val value = document.getString("value")
                    if (value != null) {
                        items.add(value)
                    }
                }

                if (isAdded) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        items
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    // Select the appropriate item in the Spinner if selectedItem is not null
                    if (!selectedItem.isNullOrEmpty()) {
                        val selectedIndex = items.indexOf(selectedItem)
                        if (selectedIndex != -1) {
                            spinner.setSelection(selectedIndex)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EditGemsFragment", "Error getting documents: ", exception)
            }
    }



    private fun getSpinnerIndex(spinner: Spinner, value: String?): Int {
        value?.let {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                    return i
                }
            }
        }
        return 0
    }


    private fun pickImageForImageView(index: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                updateImageView(requestCode, selectedImageUri)
                // Update Firestore document and Firebase Storage with the new image URL
                uploadImageToFirebaseStorage(requestCode, selectedImageUri)
            }
        }
    }

    private fun updateImageView(index: Int, uri: Uri) {
        when (index) {
            0 -> Picasso.get().load(uri).into(binding.EditGemImage1)
            1 -> Picasso.get().load(uri).into(binding.imageView2)
            2 -> Picasso.get().load(uri).into(binding.imageView3)
            3 -> Picasso.get().load(uri).into(binding.imageView4)
        }
    }

    private fun uploadImageToFirebaseStorage(index: Int, uri: Uri) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("gem_images/${selectedGem?.gemId}/image_$index")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    selectedGem?.imageUrls?.let { imageUrls ->
                        if (index < imageUrls.size) {
                            imageUrls[index] = downloadUri.toString()
                        } else {
                            imageUrls.add(downloadUri.toString())
                        }
                        updateFirestoreWithNewImageUrls()
                    }
                }.addOnFailureListener { e ->
                    Log.e("EditGemsFragment", "Error getting download URL", e)
                    Toast.makeText(
                        requireContext(),
                        "Failed to get download URL: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditGemsFragment", "Error uploading image", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateFirestoreWithNewImageUrls() {
        if (isAdded) {
            val firestore = FirebaseFirestore.getInstance()
            selectedGem?.let { gem ->
                firestore.collection("gems").document(gem.gemId!!)
                    .update("imageUrls", gem.imageUrls)
                    .addOnSuccessListener {
                        Log.d("EditGemsFragment", "Image URLs successfully updated!")
                        Toast.makeText(
                            requireContext(),
                            "Image updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditGemsFragment", "Error updating image URLs", e)
                        Toast.makeText(
                            requireContext(),
                            "Failed to update image URLs: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun updateGemData(updatedGem: Gems) {
        updatedGem.apply {
            nameId = binding.editName.selectedItem.toString()
            cutId = binding.editCut.selectedItem.toString()
            shapeId = binding.editShape.selectedItem.toString()
            compositionId = binding.editComposition.selectedItem.toString()
            treatmentId = binding.editTreatment.selectedItem.toString()
            origin = binding.editTextOrigin.text.toString()
            color = binding.editTextColor.text.toString()
            carats = binding.editTextCarats.text.toString().toDoubleOrNull() ?: 0.0
            // Update other fields as needed
        }

        // Call function to update data in Firestore
        updateFirestore(updatedGem)
    }
    private fun updateFirestore(updatedGem: Gems) {
        val firestore = FirebaseFirestore.getInstance()
        // Log updatedGem to ensure all fields are populated correctly
        Log.d("EditGemsFragment", "Updating gem: $updatedGem")
        // Update the document in Firestore
        firestore.collection("gems").document(updatedGem.gemId!!)
            .set(updatedGem) // Assuming Gems class implements Parcelable or Serializable
            .addOnSuccessListener {
                // Document successfully updated
                Log.d("EditGemsFragment", "Document successfully updated!")
                Toast.makeText(requireContext(), "Gem updated successfully", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed() // Example: Go  back to previous fragment
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("EditGemsFragment", "Error updating document", e)
                Toast.makeText(requireContext(), "Failed to update gem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}