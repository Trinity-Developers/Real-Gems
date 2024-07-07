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
        // Set up click listeners for image views
        binding.imageView1.setOnClickListener { pickImageForImageView(0) }
        binding.imageView2.setOnClickListener { pickImageForImageView(1) }
        binding.imageView3.setOnClickListener { pickImageForImageView(2) }
        binding.imageView4.setOnClickListener { pickImageForImageView(3) }

        binding.buttonAddGem.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonAddGem.isEnabled = false // Disable the add button
            addGem()
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), index)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode in 0..3) {
            val uri = data?.data
            if (uri != null) {
                if (selectedImages.size > requestCode) {
                    selectedImages[requestCode] = uri
                } else {
                    selectedImages.add(requestCode, uri)
                }
                updateImageView(requestCode, uri)
            }
        }
    }
    private fun updateImageView(index: Int, uri: Uri) {
        when (index) {
            0 -> Picasso.get().load(uri).placeholder(R.drawable.gems_splash).error(R.drawable.gems_splash).into(binding.imageView1)
            1 -> Picasso.get().load(uri).placeholder(R.drawable.gems_splash).error(R.drawable.gems_splash).into(binding.imageView2)
            2 -> Picasso.get().load(uri).placeholder(R.drawable.gems_splash).error(R.drawable.gems_splash).into(binding.imageView3)
            3 -> Picasso.get().load(uri).placeholder(R.drawable.gems_splash).error(R.drawable.gems_splash).into(binding.imageView4)
        }
    }

    private fun updateImageViews() {
        val imageViews = listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4)

        imageViews.forEach { imageView ->
            Picasso.get()
                .load(R.drawable.gems_splash)
                .into(imageView)
        }

        selectedImages.forEachIndexed { index, uri ->
            if (index < imageViews.size) {
                Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.gems_splash)
                    .error(R.drawable.gems_splash)
                    .into(imageViews[index])
            }
        }
    }


    private fun addGem() {
        if (selectedImages.size != 4) {
            binding.progressBar.visibility = View.GONE
            binding.buttonAddGem.isEnabled = true // Re-enable the add button
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
                Toast.makeText(context, "Gem added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.buttonAddGem.isEnabled = true // Re-enable the add button
                Log.w("AddFragment", "Error adding document", e)
                Toast.makeText(context, "Error adding gem", Toast.LENGTH_SHORT).show()
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
                            firestore.collection("gems").document(gemId)
                                .update("imageUrls", imageUrls)
                                .addOnSuccessListener {
                                    binding.progressBar.visibility = View.GONE
                                    binding.buttonAddGem.isEnabled = true // Re-enable the add button
                                    Toast.makeText(context, "Gem added successfully", Toast.LENGTH_SHORT).show()
                                    clearFields()
                                }
                                .addOnFailureListener { e ->
                                    binding.progressBar.visibility = View.GONE
                                    binding.buttonAddGem.isEnabled = true // Re-enable the add button
                                    Log.w("AddFragment", "Error updating document", e)
                                    Toast.makeText(context, "Error adding gem", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility = View.GONE
                    binding.buttonAddGem.isEnabled = true // Re-enable the add button
                    Log.w("AddFragment", "Error uploading image", e)
                    Toast.makeText(context, "Error uploading images", Toast.LENGTH_SHORT).show()
                }
        }
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