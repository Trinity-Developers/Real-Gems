package com.trinitydevelopers.realgemsadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentAddDropDownBinding
import com.trinitydevelopers.realgemsadmin.pojos.ListItem

class AddDropDownFragment : Fragment() {
    private lateinit var binding:FragmentAddDropDownBinding

    private val firestore = FirebaseFirestore.getInstance()
    private val types = listOf("names", "cuts", "shapes", "compositions", "treatments")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentAddDropDownBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTypeSpinner()

        binding.buttonAddItem.setOnClickListener {
            addItemToCollection()
        }
    }
    private fun setupTypeSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
    }
    private fun addItemToCollection() {
        val selectedType = binding.spinnerType.selectedItem.toString()
        val value = binding.editTextValue.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (value.isEmpty()) {
            Toast.makeText(context, "Value cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putString("selectedType", selectedType)
            putString("value", value)
            putString("description", description)

        }
        val allGemsFragment = AllGemsFragment().apply {
            arguments = bundle

        }
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frame_container, allGemsFragment)
            ?.addToBackStack(null)
            ?.commit()

        val listItem = ListItem(
            value = value,
            description = description
        )

        firestore.collection("lists").document(selectedType).collection("items")
            .add(listItem)
            .addOnSuccessListener {
                Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show()
                binding.editTextValue.text.clear()
                binding.editTextDescription.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding item: $e", Toast.LENGTH_SHORT).show()
            }
    }



}