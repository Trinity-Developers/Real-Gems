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
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter
import com.trinitydevelopers.realgemsadmin.adapter.GemsAdapter
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentExploreBinding
import com.trinitydevelopers.realgemsadmin.fragments.SearchFragment
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class ExploreFragment : Fragment() {
    private lateinit var binding:FragmentExploreBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var pinnedProductAdapter: ProductAdapter

    private var gemsList = mutableListOf<Gems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentExploreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        //categories
        setupRecyclerView()
        fetchGemsData()
        //pinned products
        setupPinnedProductRecyclerView()
        fetchGemsPinnedProductData()

        binding.edtExploreSearch.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, SearchFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    //categories
    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesAdapter(requireContext(), gemsList)
        binding.gemsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        binding.gemsRV.adapter = categoriesAdapter
    }
    private fun fetchGemsData() {
        firestore.collection("gems")
            .get()
            .addOnSuccessListener { documents ->
                gemsList.clear()
                for (document in documents) {
                    val gem = document.toObject(Gems::class.java)
                    gemsList.add(gem)
                }
                categoriesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }

    //pinned products
    private fun setupPinnedProductRecyclerView() {
        pinnedProductAdapter = ProductAdapter(requireContext(), gemsList)
        binding.pinnedProductRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        binding.pinnedProductRV.adapter = pinnedProductAdapter
    }

    private fun fetchGemsPinnedProductData() {
        firestore.collection("gems")
            .get()
            .addOnSuccessListener { documents ->
                gemsList.clear()
                for (document in documents) {
                    val gem = document.toObject(Gems::class.java)
                    gemsList.add(gem)
                }
                pinnedProductAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }
}