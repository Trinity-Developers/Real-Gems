package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter
import com.trinitydevelopers.realgemsadmin.adapter.GemsAdapter
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentExploreBinding
import com.trinitydevelopers.realgemsadmin.fragments.AllGemsFragment
import com.trinitydevelopers.realgemsadmin.fragments.GemsCategoryFragment
import com.trinitydevelopers.realgemsadmin.fragments.GemsDetailFragment
import com.trinitydevelopers.realgemsadmin.fragments.SearchFragment
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class ExploreFragment : Fragment() {
    private lateinit var binding: FragmentExploreBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var pinnedProductAdapter: ProductAdapter

    private var gemsList = mutableListOf<Gems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()



        binding.tvExploreSeeAllCategories.setOnClickListener {
            navigateToGemsCategory()
        }

        //categories
        setupRecyclerView()
        fetchGemsData()
        //pinned products
        setupPinnedProductRecyclerView()
        fetchGemsPinnedProductData()


        binding.edtExploreSearch.setOnClickListener {
            navigateToSearchFragment()
        }
    }

    //categories
    private fun navigateToGemsCategory() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, GemsCategoryFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesAdapter(requireContext(), gemsList) { selectedCategory ->
            navigateToAllGems(selectedCategory)
        }
        binding.gemsRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.gemsRV.adapter = categoriesAdapter
    }

    private fun fetchGemsData() {
        firestore.collection("gems")
            .get()
            .addOnSuccessListener { documents ->
                val uniqueCategoriesMap = mutableMapOf<String, Gems>()

                for (document in documents) {
                    val gem = document.toObject<Gems>()
                    val gemId = document.id // Retrieve Firestore document ID
                    gem.gemId = gemId // Assign Firestore document ID to gemId field

                    val category = gem.nameId ?: continue

                    if (!uniqueCategoriesMap.containsKey(category)) {
                        uniqueCategoriesMap[category] = gem
                    }
                }

                val uniqueCategoriesList = uniqueCategoriesMap.values.toList()
                updateRecyclerView(uniqueCategoriesList)
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }


    private fun updateRecyclerView(categories: List<Gems>) {
        categoriesAdapter.submitList(categories)
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

    private fun setupPinnedProductRecyclerView() {
        pinnedProductAdapter = ProductAdapter(requireContext(), listOf()) { selectedGem ->
            navigateToGemsDetail(selectedGem)
        }
        binding.pinnedProductRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.pinnedProductRV.adapter = pinnedProductAdapter
    }
    private fun navigateToGemsDetail(selectedGem: Gems) {
        val bundle = Bundle().apply {
            putSerializable("selectedGem", selectedGem)
        }
        val detailFragment = GemsDetailFragment().apply {
            arguments = bundle
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, detailFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun fetchGemsPinnedProductData() {
        firestore.collection("gems")
            .whereEqualTo("pinned", true) // Assuming "isPinned" is the field in Firestore
            .get()
            .addOnSuccessListener { documents ->
                val pinnedGems = mutableListOf<Gems>()
                for (document in documents) {
                    val gem = document.toObject<Gems>()
                    pinnedGems.add(gem)
                }
                updatePinnedProductsRecyclerView(pinnedGems)
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting pinned gems: ", exception)
            }
    }

    private fun updatePinnedProductsRecyclerView(pinnedGems: List<Gems>) {
        pinnedProductAdapter.submitList(pinnedGems)
    }


    private fun navigateToSearchFragment() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, SearchFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}