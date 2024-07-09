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
import com.trinitydevelopers.realgemsadmin.adapter.AllGemsCategoryAdapter
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentGemsCategoryBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems
import com.trinitydevelopers.realgemsadmin.pojos.ListItem

class GemsCategoryFragment : Fragment() {
    private lateinit var binding: FragmentGemsCategoryBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var categoriesAdapter: AllGemsCategoryAdapter
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
        categoriesAdapter = AllGemsCategoryAdapter(requireContext(), gemsList) { selectedCategory ->
            navigateToAllGems(selectedCategory)
        }
        binding.gemsCategoryRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.gemsCategoryRV.adapter = categoriesAdapter
    }


    private fun navigateToAllGems(selectedCategory: String) {
        firestore.collection("lists").document("names").collection("items")
            .whereEqualTo("value", selectedCategory)
            .get()
            .addOnSuccessListener { documents ->
                val listItem = if (documents.isEmpty) null else documents.documents[0].toObject(ListItem::class.java)

                val fragment = AllGemsFragment().apply {
                    arguments = Bundle().apply {
                        putString("selectedCategory", selectedCategory)
                        putSerializable("selectedCategoryDescription", listItem)
                    }
                }
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

}