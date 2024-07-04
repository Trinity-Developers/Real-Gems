package com.trinitydevelopers.realgemsadmin.fragments

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentGemsCategoryBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class GemsCategoryFragment : Fragment() {
    private lateinit var binding: FragmentGemsCategoryBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var gemsList = mutableListOf<Gems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentGemsCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        //categories
        setupRecyclerView()
        fetchGemsData()
    }


    private fun fetchGemsData() {
        firestore.collection("gems")
            .get()
            .addOnSuccessListener { documents ->
                val uniqueCategoriesMap = mutableMapOf<String, Gems>()

                for (document in documents) {
                    val gem = document.toObject(Gems::class.java)
                    val category = gem.nameId ?: continue

                    if (!uniqueCategoriesMap.containsKey(category)) {
                        uniqueCategoriesMap[category] = gem
                    }
                }

                val uniqueCategoriesList = uniqueCategoriesMap.values.toList()
                updateRecyclerView(uniqueCategoriesList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun updateRecyclerView(categories: List<Gems>) {
        categoriesAdapter.submitList(categories)
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesAdapter(requireContext(), gemsList) { selectedCategory ->
            navigateToAllGems(selectedCategory)
        }
        binding.gemsCategoryRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.gemsCategoryRV.adapter = categoriesAdapter
    }

    private fun navigateToAllGems(selectedCategory: String) {
        val fragment = AllGemsFragment().apply {
            arguments = Bundle().apply {
                putString("selectedCategory", selectedCategory)
            }
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}