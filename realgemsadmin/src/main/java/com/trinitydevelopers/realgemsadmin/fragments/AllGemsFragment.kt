package com.trinitydevelopers.realgemsadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentAllGemsBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems


class AllGemsFragment : Fragment() {
   private lateinit var binding: FragmentAllGemsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var productAdapter: ProductAdapter
    private var gemsList = mutableListOf<Gems>()
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentAllGemsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        selectedCategory = arguments?.getString("selectedCategory")

        setupRecyclerView()
        fetchGemsData()
    }

    private fun fetchGemsData() {
        selectedCategory?.let { category ->
            firestore.collection("gems")
                .whereEqualTo("nameId", category)
                .get()
                .addOnSuccessListener { documents ->
                    gemsList.clear()
                    for (document in documents) {
                        val gem = document.toObject(Gems::class.java)
                        gemsList.add(gem)
                    }
                    updateRecyclerView()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to load gems: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun updateRecyclerView() {
        productAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(requireContext(), gemsList) { gem ->
            navigateToDetailFragment(gem)
        }
        binding.allGemsFromCategoriesRV.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.allGemsFromCategoriesRV.adapter = productAdapter
    }

    private fun navigateToDetailFragment(gem: Gems) {
        val detailFragment = GemsDetailFragment()
        val bundle = Bundle().apply {
            putSerializable("selectedGem", gem)
        }
        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}