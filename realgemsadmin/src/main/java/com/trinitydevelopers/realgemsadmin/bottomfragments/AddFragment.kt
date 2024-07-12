package com.trinitydevelopers.realgemsadmin.bottomfragments

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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentAddBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val selectedImages = mutableListOf<Uri>()
    private val PICK_IMAGES_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()

        binding.progressBar.visibility = View.GONE
        // Set up click listeners for image buttons
        binding.imageView1.setOnClickListener { pickImageForImageView(0) }
        binding.imageView2.setOnClickListener { pickImageForImageView(1) }
        binding.imageView3.setOnClickListener { pickImageForImageView(2) }
        binding.imageView4.setOnClickListener { pickImageForImageView(3) }

        binding.buttonAddGem.setOnClickListener {
            if (binding.editTextOrigin.text.isEmpty()
                || binding.editTextColor.text.isEmpty()
                || binding.editTextCarats.text.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonAddGem.isEnabled = false // Disable the add button
                binding.spinnerName.isEnabled=false
                binding.spinnerCut.isEnabled=false
                binding.spinnerShape.isEnabled=false
                binding.spinnerTreatment.isEnabled=false
                binding.spinnerComposition.isEnabled=false
                binding.editTextColor.isEnabled=false
                binding.editTextCarats.isEnabled=false
                binding.editTextOrigin.isEnabled=false
                binding.imageView1.isEnabled=false
                binding.imageView2.isEnabled=false
                binding.imageView3.isEnabled=false
                binding.imageView4.isEnabled=false
                addGem()
            }
        }
    }

    private fun setupSpinners() {
        setupSpinner(binding.spinnerName, "names")
        setupSpinner(binding.spinnerCut, "cuts")
        setupSpinner(binding.spinnerShape, "shapes")
        setupSpinner(binding.spinnerComposition, "compositions")
        setupSpinner(binding.spinnerTreatment, "treatments")
    }
    private fun setupSpinner(spinner: Spinner, collection: String) {
        firestore.collection("lists").document(collection).collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.map { it.getString("value") ?: "" }
                if (isAdded) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        items
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.w("AddFragment", "Error getting documents: ", exception)
            }
    }

    private fun pickImageForImageView(index: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), index) // Use index as requestCode
    }



    private var imageCount = 0



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode in 0..3) {
            val uri = data?.data
            if (uri != null) {
                // Check if this image is already selected
                if (!selectedImages.contains(uri)) {
                    // Add the image to the correct position based on requestCode
                    if (selectedImages.size < 4) {
                        if (selectedImages.size > requestCode) {
                            selectedImages[requestCode] = uri
                        } else {
                            selectedImages.add(requestCode, uri)
                        }
                        updateImageView(requestCode, uri) // Use requestCode to update the correct ImageView
                    } else {
                        Toast.makeText(context, "You have already selected 4 images", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "This image is already selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateImageView(index: Int, uri: Uri) {
        when (index) {
            0 -> Picasso.get().load(uri).placeholder(R.drawable.firoza).error(R.drawable.firoza).into(binding.imageView1)
            1 -> Picasso.get().load(uri).placeholder(R.drawable.firoza).error(R.drawable.firoza).into(binding.imageView2)
            2 -> Picasso.get().load(uri).placeholder(R.drawable.firoza).error(R.drawable.firoza).into(binding.imageView3)
            3 -> Picasso.get().load(uri).placeholder(R.drawable.firoza).error(R.drawable.firoza).into(binding.imageView4)
        }
    }



    private fun updateImageViews() {
        val imageViews = listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4)

        imageViews.forEach { imageView ->
            Picasso.get()
                .load(R.drawable.firoza)
                .into(imageView)
        }

        selectedImages.forEachIndexed { index, uri ->
            if (index < imageViews.size) {
                Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.firoza)
                    .error(R.drawable.firoza)
                    .into(imageViews[index])
            }
        }
    }

    private fun addGem() {
        if (selectedImages.size != 4) {
            binding.progressBar.visibility = View.GONE

            binding.buttonAddGem.isEnabled = true // Re-enable the add button
            binding.spinnerName.isEnabled=true
            binding.spinnerCut.isEnabled=true
            binding.spinnerShape.isEnabled=true
            binding.spinnerTreatment.isEnabled=true
            binding.spinnerComposition.isEnabled=true
            binding.editTextColor.isEnabled=true
            binding.editTextCarats.isEnabled=true
            binding.editTextOrigin.isEnabled=true
            binding.imageView1.isEnabled=true
            binding.imageView2.isEnabled=true
            binding.imageView3.isEnabled=true
            binding.imageView4.isEnabled=true
            Toast.makeText(context, "Please select exactly 4 images", Toast.LENGTH_SHORT).show()
            return
        }

        val gemsData = Gems(
            gemId = firestore.collection("gems").document().id, // Generate new gemId
            nameId = binding.spinnerName.selectedItem.toString(),
            cutId = binding.spinnerCut.selectedItem.toString(),
            origin = binding.editTextOrigin.text.toString(),
            shapeId = binding.spinnerShape.selectedItem.toString(),
            compositionId = binding.spinnerComposition.selectedItem.toString(),
            treatmentId = binding.spinnerTreatment.selectedItem.toString(),
            color = binding.editTextColor.text.toString(),
            carats = binding.editTextCarats.text.toString().toDouble(),
            imageUrls = mutableListOf() // Will be updated after uploading images
        )

        val gemRef = firestore.collection("gems").document(gemsData.gemId!!)
        gemRef.set(gemsData)
            .addOnSuccessListener {
                uploadImages(gemRef.id)
            }
            .addOnFailureListener { e ->
                handleAddGemFailure(e)
            }
    }

    private fun uploadImages(gemId: String) {
        val storageRef = storage.reference.child("gem_images/$gemId")
        val imageUrls = mutableListOf<String>()

        selectedImages.forEachIndexed { index, uri ->
            val ref = storageRef.child("image_$index")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        imageUrls.add(downloadUri.toString())
                        if (imageUrls.size == selectedImages.size) {
                            updateGemWithImageUrls(gemId, imageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    handleUploadImageFailure(e)
                }
        }
    }

    private fun updateGemWithImageUrls(gemId: String, imageUrls: List<String>) {
        firestore.collection("gems").document(gemId)
            .update("imageUrls", imageUrls)
            .addOnSuccessListener {
                handleAddGemSuccess()
            }
            .addOnFailureListener { e ->
                handleUpdateGemFailure(e)
            }
    }

    private fun handleAddGemFailure(e: Exception) {
        binding.progressBar.visibility = View.GONE
        binding.buttonAddGem.isEnabled = true // Re-enable the add button

        Log.w("AddFragment", "Error adding document", e)
        Toast.makeText(context, "Error adding gem", Toast.LENGTH_SHORT).show()
    }

    private fun handleUploadImageFailure(e: Exception) {
        binding.progressBar.visibility = View.GONE
        binding.buttonAddGem.isEnabled = true // Re-enable the add button
        Log.w("AddFragment", "Error uploading image", e)
        Toast.makeText(context, "Error uploading images", Toast.LENGTH_SHORT).show()
    }

    private fun handleUpdateGemFailure(e: Exception) {
        binding.progressBar.visibility = View.GONE
        binding.buttonAddGem.isEnabled = true // Re-enable the add button
        Log.w("AddFragment", "Error updating document", e)
        Toast.makeText(context, "Error adding gem", Toast.LENGTH_SHORT).show()
    }

    private fun handleAddGemSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.buttonAddGem.isEnabled = true // Re-enable the add button
        Toast.makeText(context, "Gem added successfully", Toast.LENGTH_SHORT).show()
        clearFields()
    }


    private fun clearFields() {
        binding.editTextOrigin.text.clear()
        binding.editTextColor.text.clear()
        binding.editTextCarats.text.clear()
        binding.spinnerName.setSelection(0)
        binding.spinnerCut.setSelection(0)
        binding.spinnerShape.setSelection(0)
        binding.spinnerComposition.setSelection(0)
        binding.spinnerTreatment.setSelection(0)
        selectedImages.clear()
        updateImageViews()
    }
}