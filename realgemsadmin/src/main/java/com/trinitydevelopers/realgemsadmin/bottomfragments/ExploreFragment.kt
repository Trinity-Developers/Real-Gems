package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter
import com.trinitydevelopers.realgemsadmin.adapter.PinProductAdapter
import com.trinitydevelopers.realgemsadmin.adapter.ProductAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentExploreBinding
import com.trinitydevelopers.realgemsadmin.fragments.AllGemsFragment
import com.trinitydevelopers.realgemsadmin.fragments.GemsCategoryFragment
import com.trinitydevelopers.realgemsadmin.fragments.GemsDetailFragment
import com.trinitydevelopers.realgemsadmin.pojos.Gems
import com.trinitydevelopers.realgemsadmin.pojos.ListItem

class ExploreFragment : Fragment() {
    private lateinit var binding: FragmentExploreBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var productAdapter: PinProductAdapter

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

        val text = "Real Gems"
        val spannableString = SpannableString(text)

        // Set color for "Constitution"
        val saffronColor = ContextCompat.getColor(requireContext(), R.color.red)
        spannableString.setSpan(
            ForegroundColorSpan(saffronColor),
            0,
            4,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set color for "O F"
        val blueColor = ContextCompat.getColor(requireContext(), R.color.purple)
        spannableString.setSpan(
            ForegroundColorSpan(blueColor),
            5,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to a TextView
        binding.realGemsTv.text = spannableString


        //image slider
        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel(R.drawable.image_three, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.image_two, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.images, ScaleTypes.FIT))

        val imageSlider=binding.imageSlider
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                val itemPosition=imageList[position]
                val itemMessage="Selected image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
                // You can listen here.
            }
            override fun doubleClick(position: Int) {
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
            } })


        binding.tvExploreSeeAllCategories.setOnClickListener {
            navigateToGemsCategory()
        }

        //categories
        setupRecyclerView()
        fetchGemsData()
        //pinned products
        setupPinnedProductRecyclerView()
        fetchGemsPinnedProductData()


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
        firestore.collection("lists").document("names").collection("items")
            .whereEqualTo("value", selectedCategory)
            .get()
            .addOnSuccessListener { documents ->
                val listItem = if (documents.isEmpty) null else documents.documents[0].toObject(
                    ListItem::class.java)

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
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }


    private fun setupPinnedProductRecyclerView() {
        productAdapter = PinProductAdapter(requireContext(), listOf()) { selectedGem ->
            navigateToGemsDetail(selectedGem)
        }
        binding.pinnedProductRV.layoutManager =
            GridLayoutManager(context, 2)
        binding.pinnedProductRV.adapter = productAdapter
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
        productAdapter.submitList(pinnedGems)
    }



}