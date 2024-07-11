package com.trinitydevelopers.realgemsadmin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.ImagePagerAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentGemsDetailBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class GemsDetailFragment : Fragment() {
    private lateinit var binding: FragmentGemsDetailBinding
    private lateinit var selectedGem: Gems
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve selectedGem from arguments
        arguments?.let {
            selectedGem = it.getSerializable("selectedGem") as Gems
        } ?: throw IllegalArgumentException("Selected gem must not be null")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentGemsDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate UI with gem details
        populateGemDetails(selectedGem)

        // Set initial pin/unpin icon state
        updatePinIcon(selectedGem.pinned)


        // Handle delete button click
        binding.gemsDetailDelete.setOnClickListener {
            deleteGem()
        }

        // Handle edit button click
        binding.gemsDetailEdit.setOnClickListener {
            navigateToEditFragment()
        }

        // Handle pin/unpin button click
        binding.gemsDetailPinUnpin.setOnClickListener {
            togglePinStatus()
        }

    }

    private fun populateGemDetails(gem: Gems) {
        binding.gemsDetailName.text = gem.nameId
        binding.gemsDetailCarats.text = gem.carats.toString()
        binding.gemsDetailComposition.text = gem.compositionId
        binding.gemsDetailCut.text = gem.cutId
        binding.gemsDetailOrigin.text = gem.origin
        binding.gemsDetailShape.text = gem.shapeId
        binding.gemsDetailTreatment.text = gem.treatmentId
        binding.gemsDetailColor.text = gem.color
        binding.textView21.text="${gem.compositionId} and certificate ${gem.nameId} dd${gem.carats} Carats in ${gem.shapeId} shape and ${gem.color} color."

        // Load images into ViewPager2
        val viewPager = binding.viewPager
        val imageUrls = gem.imageUrls
        viewPager.adapter = ImagePagerAdapter(imageUrls)
    }
    private fun togglePinStatus() {
        selectedGem.pinned = !selectedGem.pinned

        firestore.collection("gems")
            .document(selectedGem.gemId!!)
            .update("pinned", selectedGem.pinned)
            .addOnSuccessListener {
                updatePinIcon(selectedGem.pinned)
                Toast.makeText(requireContext(), if (selectedGem.pinned) "Pinned" else "Unpinned", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("GemsDetailFragment", "Error updating pin status", e)
                Toast.makeText(requireContext(), "Error updating pin status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePinIcon(isPinned: Boolean) {
        if (isPinned) {
            binding.gemsDetailPinUnpin.setImageResource(R.drawable.pinned_icon)
        } else {
            binding.gemsDetailPinUnpin.setImageResource(R.drawable.unpinned_icon)
        }
    }
    private fun deleteGem() {
        val firestore = FirebaseFirestore.getInstance()
        val gemId = selectedGem.gemId ?: return // Exit if gemId is null

        firestore.collection("gems").document(selectedGem.gemId!!)
            .delete()
            .addOnSuccessListener {
                Log.d("GemsDetailFragment", "DocumentSnapshot successfully deleted!")
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()

                // Navigate back or refresh the list of gems
                activity?.onBackPressed() // Example: Go back to previous fragment
            }
            .addOnFailureListener { e ->
                Log.e("GemsDetailFragment", "Error deleting document", e)
                Toast.makeText(requireContext(), "Error deleting gem", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToEditFragment() {
        val bundle = Bundle()
        bundle.putSerializable("selectedGem", selectedGem)

        val editFragment = EditGemsFragment()
        editFragment.arguments = bundle

        // Navigate to EditGemFragment
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frame_container, editFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
